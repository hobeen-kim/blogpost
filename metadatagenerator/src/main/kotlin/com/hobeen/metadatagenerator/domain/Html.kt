package com.hobeen.metadatagenerator.domain

import java.time.LocalDateTime

data class Html (
    val title: String,
    val pubDate: LocalDateTime,
    val thumbnail: String,
    val tags: List<String>,
    val description: String,
)