package com.hobeen.domain.like

import com.hobeen.domain.post.PostId

data class LikeId(
    val userId: UserId,
    val postId: PostId,
)