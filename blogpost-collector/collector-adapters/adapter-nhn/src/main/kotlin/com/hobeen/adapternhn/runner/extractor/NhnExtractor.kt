package com.hobeen.adapternhn.runner.extractor

import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.collectorcommon.domain.Message
import com.hobeen.collectorengine.port.Extractor
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

@Component
class NhnExtractor(
    val objectMapper: ObjectMapper
): Extractor {

    override fun extract(
        crawlingResult: CrawlingResult,
        source: String
    ): List<Message> {

        println(objectMapper.registeredModuleIds)

        return crawlingResult.htmls.flatMap { json ->
            val nhnJson = objectMapper.readValue(json, NhnJson::class.java)

            nhnJson.posts.map { post ->
                Message(
                    source = source,
                    title = post.postPerLang.title,
                    url = "https://meetup.nhncloud.com/posts/${post.postId}",
                    pubDate = post.publishTime.toLocalDateTime(),
                    tags = post.postPerLang.tag.split(",").map { it.trim().replace("#", "") },
                    description = post.postPerLang.description,
                    thumbnail = post.postPerLang.repImageUrl
                )
            }
        }
    }

}