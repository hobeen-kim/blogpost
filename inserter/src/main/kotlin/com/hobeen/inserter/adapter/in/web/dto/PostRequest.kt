package com.hobeen.inserter.adapter.`in`.web.dto

import java.time.LocalDateTime

class PostRequest (
    val title: String,
    val source: String,
    val url: String,
    val pubDate: LocalDateTime,
    val tags: List<String>,
    val description: String,
    val thumbnail: String,
    val content: String,
    val abstractContent: String,
)