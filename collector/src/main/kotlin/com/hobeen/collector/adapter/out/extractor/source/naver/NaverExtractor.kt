package com.hobeen.collector.adapter.out.extractor.source.naver

import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.collector.application.port.`in`.dto.ExtractorProps
import com.hobeen.collector.application.port.out.Extractor
import com.hobeen.collector.application.port.out.dto.CrawlingResult
import com.hobeen.collector.domain.Message
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
class NaverExtractor(
    val objectMapper: ObjectMapper
): Extractor {

    override fun extract(
        crawlingResult: CrawlingResult,
        source: String,
        props: ExtractorProps,
    ): List<Message> {

        return crawlingResult.htmls.flatMap { json ->
            val naverJson = objectMapper.readValue(json, NaverJson::class.java)

            naverJson.content.map { post ->
                Message(
                    source = source,
                    title = post.postTitle,
                    url = "https://d2.naver.com${post.url}",
                    pubDate = getDateTime(post.postPublishedAt),
                    tags = listOf(),
                    description = post.postHtml,
                    thumbnail = post.postImage?.let { "https://d2.naver.com${post.postImage}" },
                )
            }
        }
    }

    private fun getDateTime(pubLong: Long): LocalDateTime {
        return LocalDateTime.ofEpochSecond(pubLong / 1000, 0, ZoneOffset.of("+09:00"))
    }

}