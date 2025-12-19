package com.hobeen.dlqprocessor.domain

import java.time.LocalDateTime

data class RawMessage(
    val title: String?,
    val source: String,
    val url: String,
    val pubDate: LocalDateTime?,
    val tags: List<String>,
    val description: String?,
    val thumbnail: String?,
): Message {

    override fun key(): String {
        return source
    }
}