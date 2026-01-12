package com.hobeen.collector.domain

import java.time.LocalDateTime

data class Message (
    val source: String,
    val title: String?,
    var url: String,
    val pubDate: LocalDateTime?,
    val tags: List<String>,
    var description: String?,
    val thumbnail: String?,
)