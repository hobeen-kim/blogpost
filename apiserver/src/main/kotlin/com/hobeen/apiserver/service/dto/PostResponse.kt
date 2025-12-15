package com.hobeen.apiserver.service.dto

import java.time.LocalDateTime

data class PostResponse (
    val postId: Long,
    val title: String,
    val source: String,
    val url: String,
    val pubDate: LocalDateTime,
    val description: String,
    val thumbnail: String,
    val tags: List<String>,
)