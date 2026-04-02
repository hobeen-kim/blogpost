package com.hobeen.apiserver.repository

import com.hobeen.apiserver.entity.PostView
import org.springframework.data.jpa.repository.JpaRepository

interface PostViewRepository : JpaRepository<PostView, Long> {
    fun countByUserId(userId: String): Long
    fun findTopByUserIdOrderByViewedAtAsc(userId: String): PostView?
}
