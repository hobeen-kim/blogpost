package com.hobeen.domain.post

import com.hobeen.domain.tag.Tag
import java.time.LocalDateTime

class Post (
    val postId: PostId,
    val title: String,
    val source: String,
    val url: String,
    val pubDate: LocalDateTime,
    val description: String,
    val thumbnail: String,
    val tags: List<Tag>
)