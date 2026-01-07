package com.hobeen.apiserver.service

import com.hobeen.apiserver.entity.Bookmark
import com.hobeen.apiserver.entity.BookmarkGroup
import com.hobeen.apiserver.entity.BookmarkId
import com.hobeen.apiserver.repository.BookmarkGroupRepository
import com.hobeen.apiserver.repository.BookmarkRepository
import com.hobeen.apiserver.repository.PostRepository
import com.hobeen.apiserver.service.dto.BookmarkGroupResponse
import com.hobeen.apiserver.service.dto.BookmarkGroupWithPostResponse
import com.hobeen.apiserver.service.dto.PostBookmarkResponse
import com.hobeen.apiserver.service.dto.SliceResponse
import com.hobeen.apiserver.util.exception.BookmarkGroupCreateException
import com.hobeen.apiserver.util.exception.BookmarkGroupDuplicateException
import com.hobeen.apiserver.util.exception.BookmarkGroupNotFoundException
import com.hobeen.apiserver.util.exception.BookmarkGroupUpdateException
import com.hobeen.apiserver.util.exception.BookmarkNotFoundException
import com.hobeen.apiserver.util.exception.PostNotFoundException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class BookmarkService(
    private val bookmarkRepository: BookmarkRepository,
    private val bookmarkGroupRepository: BookmarkGroupRepository,
    private val postRepository: PostRepository,

    private val metadataService: MetadataService,
) {
    companion object {
        const val DEFAULT_GROUP_NAME = "기본"
        const val BOOKMARK_GROUP_MAX_COUNT = 10
    }

    fun bookmark(postId: Long, userId: String, bookmarkGroupId: Long?) {

        val post = postRepository.findById(postId).orElseThrow { PostNotFoundException(postId) }
        val bookmarkGroup = getBookmarkGroup(userId, bookmarkGroupId)

        bookmarkRepository.save(
            Bookmark.create(bookmarkGroup, post)
        )
    }

    fun createBookmarkGroup(name: String, userId: String): Long {

        if(name.isBlank()) throw BookmarkGroupCreateException("유효하지 않은 이름입니다. : $name")
        if(isExceedMaxGroupCount(userId)) throw BookmarkGroupCreateException("북마크 최대개수 초과")

        val bookmarkGroup = try {
            bookmarkGroupRepository.save(
                BookmarkGroup(
                    name = name,
                    userId = userId,
                )
            )
        } catch (e: DataIntegrityViolationException) {
            throw BookmarkGroupDuplicateException(name)
        }

        return bookmarkGroup.bookmarkGroupId!!
    }

    fun updateBookmarkGroup(bookmarkGroupId: Long, userId: String, updatedName: String) {
        val bookmarkGroup = bookmarkGroupRepository.findByUserIdAndBookmarkGroupId(userId = userId, bookmarkGroupId = bookmarkGroupId) ?: throw BookmarkGroupNotFoundException(bookmarkGroupId)

        bookmarkGroup.updateName(updatedName)
    }

    /**
     * @param postId : bookmark 의 postId
     * @param userId : 요청자
     * @param bookmarkGroupId : 북마크 그룹, null 이면 post 의 요청자의 bookmark 를 모두 삭제
     */
    fun deleteBookmark(postId: Long, userId: String, bookmarkGroupId: Long?) {

        //bookmarkGroupId 가 null 이면 post 와 관련된 모든 bookmark 를 삭제한다.
        if (bookmarkGroupId == null) {
            bookmarkRepository.deleteBookmarksByUserIdAndPostId(userId, postId)
            return
        }

        //bookmarkGroup 이 없으면 exception
        if (!bookmarkGroupRepository.existsByUserIdAndBookmarkGroupId(userId, bookmarkGroupId)) {
            throw BookmarkGroupNotFoundException(bookmarkGroupId)
        }

        //bookmark 삭제
        bookmarkRepository.deleteById(BookmarkId(postId, bookmarkGroupId))
    }

    fun deleteBookmarkGroup(userId: String, bookmarkGroupId: Long) {
        bookmarkGroupRepository.deleteByUserIdAndBookmarkGroupId(userId, bookmarkGroupId)
    }

    @Transactional(readOnly = true)
    fun getBookmarks(userId: String, cursor: LocalDateTime?, size: Int, bookmarkGroupId: Long? = null): SliceResponse<PostBookmarkResponse> {

        val bookmarks = if(bookmarkGroupId == null) {
            bookmarkRepository.findAllByLastCreatedTime(userId, cursor ?: LocalDateTime.now(), size)
        } else {
            bookmarkRepository.findAllByLastCreatedTime(userId, cursor ?: LocalDateTime.now(), size, bookmarkGroupId)
        }

        val bookmarkMap = bookmarks.data.associateBy { it.id.postId }

        return SliceResponse(
            data = bookmarks.data.map {

                val bookmark = bookmarkMap[it.post.postId] ?: throw IllegalArgumentException("bookmark map error")
                val metadata = metadataService.getMetadata(it.post.source)

                PostBookmarkResponse.of(it.post, metadata, bookmark.createdAt)
            },
            size = bookmarks.data.size,
            hasNext = bookmarks.hasNext,
        )
    }

    @Transactional(readOnly = true)
    fun getBookmarkGroups(userId: String): List<BookmarkGroupResponse> {

        val bookmarkGroups = bookmarkGroupRepository.findByUserId(userId)

        //empty 면 기본 폴더를 만들고 return
        if(bookmarkGroups.isEmpty()) {
            val newBookmarkGroups = listOf(createDefaultGroup(userId))

            return newBookmarkGroups.map { BookmarkGroupResponse.of(it) }
        }

        return bookmarkGroups.map { BookmarkGroupResponse.of(it) }.sorted()
    }

    @Transactional(readOnly = true)
    fun getBookmarkGroupsWithPostInfo(userId: String, postId: Long): List<BookmarkGroupWithPostResponse> {

        val bookmarkGroups = bookmarkGroupRepository.findByUserId(userId)

        //empty 면 기본 폴더를 만들고 return
        if(bookmarkGroups.isEmpty()) {
            val bookmarkGroups = listOf(createDefaultGroup(userId))

            return bookmarkGroups.map { BookmarkGroupWithPostResponse.of(it, false) }
        }

        //bookmarkGroup 을 돌면서 postId 를 가지고 있는지 확인
        return bookmarkGroups.map { bookmarkGroup ->
            val hasPost = bookmarkGroup.bookmarks.any { it.id.postId == postId }
            BookmarkGroupWithPostResponse.of(bookmarkGroup, hasPost)
        }.sorted()
    }

    private fun getBookmarkGroup(userId: String, bookmarkGroupId: Long?): BookmarkGroup {
        return bookmarkGroupId?.let {
            bookmarkGroupRepository.findById(it).orElseThrow { IllegalArgumentException("bookmark group not found") }
        } ?: getDefaultBookmarkGroup(userId)
    }

    private fun getDefaultBookmarkGroup(userId: String): BookmarkGroup {
        return bookmarkGroupRepository.findByUserIdAndName(userId, DEFAULT_GROUP_NAME) ?: createDefaultGroup(userId)
    }

    private fun createDefaultGroup(userId: String): BookmarkGroup {
        return bookmarkGroupRepository.save(
            BookmarkGroup(
                name = DEFAULT_GROUP_NAME,
                userId = userId,
            )
        )
    }

    private fun isExceedMaxGroupCount(userId: String): Boolean {
        return bookmarkGroupRepository.countByUserId(userId) >= BOOKMARK_GROUP_MAX_COUNT
    }
}
