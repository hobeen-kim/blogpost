package com.hobeen.apiserver.repository

import com.hobeen.apiserver.entity.Comment
import com.hobeen.apiserver.repository.dto.SliceDataDto
import java.time.LocalDateTime

interface CommentRepositoryCustom {

    fun findAllByLastCreatedTime(
        postId: Long,
        cursorCreatedAt: LocalDateTime,
        limit: Int
    ): SliceDataDto<Comment>

    fun existsByPostIds(
        userId: String,
        postIds: List<Long>,
    ): Map<Long, Boolean>
}