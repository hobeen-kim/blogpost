package com.hobeen.inserter.domain

import java.time.LocalDateTime

data class EnrichedMessage (
    val title: String,
    val source: String,
    val url: String,
    val pubDate: LocalDateTime,
    val tags: List<String>,
    val description: String,
    val thumbnail: String,
)