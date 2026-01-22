package com.hobeen.domain.comment

import com.hobeen.domain.post.PostId

class Comment (
    val commentId: CommentId,
    val postId: PostId,
    val userId: String,
    var name: String,
    var comment: String,
)