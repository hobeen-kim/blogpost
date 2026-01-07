package com.hobeen.apiserver.service

import com.hobeen.apiserver.entity.Bookmark
import com.hobeen.apiserver.entity.BookmarkGroup
import com.hobeen.apiserver.entity.BookmarkId
import com.hobeen.apiserver.repository.BookmarkGroupRepository
import com.hobeen.apiserver.repository.BookmarkRepository
import com.hobeen.apiserver.repository.PostRepository
import com.hobeen.apiserver.service.dto.BookmarkGroupResponse
import com.hobeen.apiserver.service.dto.PostBookmarkResponse
import com.hobeen.apiserver.service.dto.SliceResponse
import com.hobeen.apiserver.util.exception.BookmarkGroupNotFoundException
import com.hobeen.apiserver.util.exception.PostNotFoundException
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
    }

    fun bookmark(postId: Long, userId: String, bookmarkGroupId: Long?) {

        val post = postRepository.findById(postId).orElseThrow { PostNotFoundException(postId) }
        val bookmarkGroup = getBookmarkGroup(userId, bookmarkGroupId)

        bookmarkRepository.save(
            Bookmark.create(bookmarkGroup, post)
        )
    }

    fun createBookmarkGroup(name: String, userId: String): Long {
        return bookmarkGroupRepository.save(
            BookmarkGroup(
                name = name,
                userId = userId,
            )
        ).bookmarkGroupId!!
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

    @Transactional(readOnly = true)
    fun getBookmarks(userId: String, cursor: LocalDateTime?, size: Int): SliceResponse<PostBookmarkResponse> {

        val bookmarks = bookmarkRepository.findAllByLastCreatedTime(userId, cursor ?: LocalDateTime.now(), size)

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

        return bookmarkGroupRepository.findByUserId(userId).map { BookmarkGroupResponse.of(it) }.sorted()
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
}
