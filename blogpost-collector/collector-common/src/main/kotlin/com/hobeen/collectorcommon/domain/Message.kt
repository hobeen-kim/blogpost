package com.hobeen.collectorcommon.domain

import java.time.LocalDateTime

data class Message (
    val source: String,
    val title: String?,
    val url: String,
    val pubDate: LocalDateTime?,
    val tags: List<String>,
    val description: String?,
    val thumbnail: String?,
)