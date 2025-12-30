package com.hobeen.metadatagenerator.application.port.`in`.dto

import com.hobeen.metadatagenerator.domain.Html
import java.time.LocalDateTime

data class HtmlResponse (
    val title: String,
    val pubDate: LocalDateTime?,
    val thumbnail: String?,
    val tags: List<String>,
    val description: String,
    val content: String,
    val abstractedContent: String,
) {
    companion object {
        fun of(html: Html, abstractedContent: String): HtmlResponse {
            return HtmlResponse(
                title = html.title,
                pubDate = html.pubDate,
                thumbnail = html.thumbnail,
                tags = html.tags,
                description = html.description,
                content = html.content,
                abstractedContent = abstractedContent,
            )
        }
    }
}