package com.hobeen.apiserver.service

import com.hobeen.apiserver.entity.Comment
import com.hobeen.apiserver.entity.Like
import com.hobeen.apiserver.entity.LikeId
import com.hobeen.apiserver.repository.CommentRepository
import com.hobeen.apiserver.repository.LikeRepository
import com.hobeen.apiserver.repository.PostRepository
import com.hobeen.apiserver.service.dto.CommentCreateRequest
import com.hobeen.apiserver.service.dto.CommentResponse
import com.hobeen.apiserver.service.dto.CommentUpdateRequest
import com.hobeen.apiserver.service.dto.PostLikeResponse
import com.hobeen.apiserver.service.dto.SliceResponse
import com.hobeen.apiserver.util.auth.authUserIdOrNull
import com.hobeen.apiserver.util.exception.AuthorizationException
import com.hobeen.apiserver.util.exception.CommentNotFoundException
import com.hobeen.apiserver.util.exception.PostNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class CommentService(
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,
) {

    fun createComment(postId: Long, request: CommentCreateRequest): Long {

        val post = postRepository.findById(postId).orElseThrow { PostNotFoundException(postId) }

        val comment = commentRepository.save(
            Comment.create(
                userId = request.userId,
                name = request.username,
                post = post,
                comment = request.comment,
            )
        )

        return comment.commentId!!
    }

    fun updateComment(commentId: Long, request: CommentUpdateRequest): Long {

        val comment = commentRepository.findById(commentId).orElseThrow { throw CommentNotFoundException(commentId = commentId) }

        if(comment.userId != request.userId) throw AuthorizationException(resource = "comment", id = commentId.toString())

        comment.update(
            name = request.username,
            comment = request.comment
        )

        return comment.commentId!!
    }

    fun deleteComment(commentId: Long, userId: String) {

        val comment = commentRepository.findById(commentId).orElseThrow { throw CommentNotFoundException(commentId = commentId) }

        if(comment.userId != userId) throw AuthorizationException(resource = "comment", id = commentId.toString())

        commentRepository.delete(comment)
    }

    @Transactional(readOnly = true)
    fun getComments(postId: Long, cursor: LocalDateTime?, size: Int): SliceResponse<CommentResponse> {

        val comments = commentRepository.findAllByLastCreatedTime(postId, cursor ?: LocalDateTime.now(), size)

        return SliceResponse(
            data = comments.data.map { CommentResponse.of(it, authUserIdOrNull()) },
            size = comments.data.size,
            hasNext = comments.hasNext,
        )
    }
}