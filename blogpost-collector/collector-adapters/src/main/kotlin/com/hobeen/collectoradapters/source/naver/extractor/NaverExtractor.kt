package com.hobeen.collectoradapters.source.naver.extractor

import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.collectorcommon.domain.ExtractorProps
import com.hobeen.collectorcommon.domain.Message
import com.hobeen.collectorengine.port.Extractor
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.springframework.stereotype.Component
import java.time.LocalDateTime

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
        return LocalDateTime.ofEpochSecond(pubLong / 1000, 0, java.time.ZoneOffset.of("+09:00"))
    }

}