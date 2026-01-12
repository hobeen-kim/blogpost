package com.hobeen.collector.adapter.out.extractor.source.skplanet

import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.blogpostcommon.util.localDateParse
import com.hobeen.collector.application.port.`in`.dto.ExtractorProps
import com.hobeen.collector.application.port.out.Extractor
import com.hobeen.collector.application.port.out.dto.CrawlingResult
import com.hobeen.collector.domain.Message
import org.springframework.stereotype.Component

@Component
class SkplanetExtractor (
    val objectMapper: ObjectMapper
): Extractor {

    override fun extract(
        crawlingResult: CrawlingResult,
        source: String,
        props: ExtractorProps
    ): List<Message> {
        return crawlingResult.htmls.flatMap { json ->
            val skplanetJson = objectMapper.readValue(json, SkplanetJson::class.java)

            skplanetJson.result.data.allMarkdownRemark.nodes.map { post ->
                Message(
                    source = source,
                    title = post.frontmatter.title,
                    url = "https://techtopic.skplanet.com${post.fields.slug}",
                    pubDate = localDateParse(post.frontmatter.date),
                    tags = post.frontmatter.tags,
                    description = post.excerpt,
                    thumbnail = null,
                )
            }
        }
    }
}