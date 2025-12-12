package com.hobeen.metadatagenerator.domain

import java.time.LocalDateTime

class RawMessage (
    val title: String?,
    val source: String,
    val url: String,
    val pubDate: LocalDateTime?,
    val tags: List<String>,
    val description: String?,
    val thumbnail: String?,
) {
    fun hasAllValues(): Boolean {
        return !title.isNullOrBlank()
                || pubDate != null
                || tags.isNotEmpty()
                || !description.isNullOrBlank()
                || !thumbnail.isNullOrBlank()
    }

    fun toEnrichedMessage(): EnrichedMessage {

        require(hasAllValues()) { throw IllegalArgumentException("cannot convert to enrichMessage") }

        return EnrichedMessage(
            title = title!!,
            source = source,
            url = url,
            pubDate = pubDate!!,
            tags = ArrayList<String>().apply {
                addAll(tags)
            },
            description = description!!,
            thumbnail = thumbnail!!,
        )
    }
}