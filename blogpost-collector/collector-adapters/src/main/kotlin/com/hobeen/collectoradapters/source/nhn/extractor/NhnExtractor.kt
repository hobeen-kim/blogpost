package com.hobeen.collectoradapters.source.nhn.extractor

import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.collectorcommon.domain.Message
import com.hobeen.collectorengine.port.Extractor
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.springframework.stereotype.Component

@Component
class NhnExtractor(
    val objectMapper: ObjectMapper
): Extractor {

    override fun extract(
        crawlingResult: CrawlingResult,
        source: String,
        props: Map<String, String>,
    ): List<Message> {

        return crawlingResult.htmls.flatMap { json ->
            val nhnJson = objectMapper.readValue(json, NhnJson::class.java)

            nhnJson.posts.map { post ->
                Message(
                    source = source,
                    title = post.postPerLang.title,
                    url = "https://meetup.nhncloud.com/posts/${post.postId}",
                    pubDate = post.publishTime.toLocalDateTime(),
                    tags = getTags(post.postPerLang.tag),
                    description = post.postPerLang.description,
                    thumbnail = post.postPerLang.repImageUrl
                )
            }
        }
    }

    private fun getTags(tagStr: String): List<String> {
        return tagStr // "#a, #b, #c"
            .split("#") // ["", "a, ", "b, ", "c, "]
            .map { it.trim().replace(",", "") } // ["", "a", "b", "c"]
            .filterNot { it.isBlank() } // ["a", "b", "c"]
    }

}