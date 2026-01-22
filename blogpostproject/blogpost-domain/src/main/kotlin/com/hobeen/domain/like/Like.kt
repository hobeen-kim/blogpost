package com.hobeen.domain.like

import com.hobeen.domain.post.Post
import java.time.LocalDateTime

class Like (
    val likeId: LikeId,
    val createdAt: LocalDateTime,
)