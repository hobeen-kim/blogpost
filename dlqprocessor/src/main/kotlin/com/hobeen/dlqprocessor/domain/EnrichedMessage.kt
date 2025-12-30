package com.hobeen.dlqprocessor.domain

import java.time.LocalDateTime

data class EnrichedMessage (
    val title: String,
    val source: String,
    val url: String,
    val pubDate: LocalDateTime,
    val tags: List<String>,
    val description: String,
    val thumbnail: String,
    val content: String,
    val abstractedContent: String,
): Message {

    override fun key(): String {
        return source
    }

    fun hasAllValues(): Boolean {
        return title.isNotBlank() &&
                source.isNotBlank() &&
                url.isNotBlank() &&
                thumbnail.isNotBlank() &&
                content.isNotBlank() &&
                abstractedContent.isNotBlank()
    }
}