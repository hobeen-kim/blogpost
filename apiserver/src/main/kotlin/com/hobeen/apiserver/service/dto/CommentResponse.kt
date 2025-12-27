package com.hobeen.apiserver.service.dto

import com.hobeen.apiserver.entity.Comment
import java.time.LocalDateTime

data class CommentResponse (
    val commentId: Long?,
    val name: String,
    val comment: String,
    val isMyComment: Boolean,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun of(comment: Comment, userId: String?): CommentResponse {
            return CommentResponse(
                commentId = comment.commentId,
                name = comment.name,
                comment = comment.comment,
                isMyComment = userId?.let { it == comment.userId } == true,
                createdAt = comment.createdAt,
            )
        }
    }
}