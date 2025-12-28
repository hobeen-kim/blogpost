package com.hobeen.apiserver.service

import com.hobeen.apiserver.entity.Bookmark
import com.hobeen.apiserver.entity.BookmarkId
import com.hobeen.apiserver.entity.Like
import com.hobeen.apiserver.entity.LikeId
import com.hobeen.apiserver.repository.BookmarkRepository
import com.hobeen.apiserver.repository.LikeRepository
import com.hobeen.apiserver.repository.PostRepository
import com.hobeen.apiserver.service.dto.PostBookmarkResponse
import com.hobeen.apiserver.service.dto.PostLikeResponse
import com.hobeen.apiserver.service.dto.SliceResponse
import com.hobeen.apiserver.util.exception.PostNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class LikeService(
    private val likeRepository: LikeRepository,
    private val postRepository: PostRepository,

    private val sourceService: SourceService,
) {

    fun like(postId: Long, userId: String) {

        val post = postRepository.findById(postId).orElseThrow { PostNotFoundException(postId) }

        likeRepository.save(
            Like.create(userId, post)
        )
    }

    fun deleteLike(postId: Long, userId: String) {

        val id = LikeId(postId, userId)

        likeRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun getLikes(userId: String, cursor: LocalDateTime?, size: Int): SliceResponse<PostLikeResponse> {

        val likes = likeRepository.findAllByLastCreatedTime(userId, cursor ?: LocalDateTime.now(), size)

        val likeMap = likes.data.associate { it.id.postId to it}

        return SliceResponse(
            data = likes.data.map {

                val like = likeMap[it.post.postId] ?: throw IllegalArgumentException("bookmark map error")
                val metadata =  sourceService.getMetadata(it.post.source)

                PostLikeResponse.of(it.post, metadata, like.createdAt)
            },
            size = likes.data.size,
            hasNext = likes.hasNext,
        )
    }
}