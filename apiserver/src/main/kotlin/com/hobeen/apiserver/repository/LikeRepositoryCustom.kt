package com.hobeen.apiserver.repository

import com.hobeen.apiserver.entity.Bookmark
import com.hobeen.apiserver.entity.Like
import com.hobeen.apiserver.repository.dto.SliceDataDto
import java.time.LocalDateTime

interface LikeRepositoryCustom {

    fun findAllByLastCreatedTime(
        userId: String,
        cursorCreatedAt: LocalDateTime,
        limit: Int
    ): SliceDataDto<Like>

    fun existsByPostIds(
        userId: String,
        postIds: List<Long>,
    ): Map<Long, Boolean>

    fun countsByPostIds(
        postIds: List<Long>
    ): Map<Long, Long>
}