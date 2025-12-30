package com.hobeen.metadatagenerator.domain

import java.time.LocalDateTime

class EnrichedMessage (
    val title: String,
    val source: String,
    val url: String,
    val pubDate: LocalDateTime,
    val tags: List<String>,
    val description: String,
    val thumbnail: String,
    val content: String,
    val abstractedContent: String,
)