package com.hobeen.apiserver.service

import com.hobeen.apiserver.entity.Bookmark
import com.hobeen.apiserver.entity.BookmarkId
import com.hobeen.apiserver.repository.BookmarkRepository
import com.hobeen.apiserver.repository.PostRepository
import com.hobeen.apiserver.service.dto.PostBookmarkResponse
import com.hobeen.apiserver.service.dto.SliceResponse
import com.hobeen.apiserver.util.exception.PostNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class BookmarkService(
    private val bookmarkRepository: BookmarkRepository,
    private val postRepository: PostRepository,

    private val metadataService: MetadataService,
) {

    fun bookmark(postId: Long, userId: String) {

        val post = postRepository.findById(postId).orElseThrow { PostNotFoundException(postId) }

        bookmarkRepository.save(
            Bookmark.create(userId, post)
        )
    }

    fun deleteBookmark(postId: Long, userId: String) {

        val id = BookmarkId(postId, userId)

        bookmarkRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun getBookmarks(userId: String, cursor: LocalDateTime?, size: Int): SliceResponse<PostBookmarkResponse> {

        val bookmarks = bookmarkRepository.findAllByLastCreatedTime(userId, cursor ?: LocalDateTime.now(), size)

        val bookmarkMap = bookmarks.data.associate { it.id.postId to it}

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
}