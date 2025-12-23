package com.hobeen.metadatagenerator.adapter.out

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.metadatagenerator.adapter.out.persistence.ParsePropsEntity
import com.hobeen.metadatagenerator.adapter.out.persistence.ParsePropsRepository
import com.hobeen.metadatagenerator.adapter.out.redis.RedisRepository
import com.hobeen.metadatagenerator.application.port.out.ParseHtmlMetadataPort
import com.hobeen.metadatagenerator.common.localDateParse
import com.hobeen.metadatagenerator.common.refineTitle
import com.hobeen.metadatagenerator.domain.Html
import com.hobeen.metadatagenerator.domain.ParseProps
import org.jsoup.Jsoup
import org.springframework.stereotype.Component

@Component
class DefaultParser(
    private val objectMapper: ObjectMapper,
): ParseHtmlMetadataPort {
    override fun getName(): String {
        return "default"
    }

    override fun parse(url: String, parserProps: ParseProps): Html {
        val doc = Jsoup.connect(url).get()

        val title = parserProps.getProps("title", objectMapper)?.let { getDataFrom(doc, it) } ?: ""
        val description = parserProps.getProps("description", objectMapper)?.let { getDataFrom(doc, it) } ?: ""
        val thumbnail = parserProps.getProps("thumbnail", objectMapper)?.let { getDataFrom(doc, it) }
        val pubDateStr = parserProps.getProps("pubDate", objectMapper)?.let { getDataFrom(doc, it) }
        val pubDate = localDateParse(pubDateStr)
        val tags = parserProps.getProps("tag", objectMapper)?.let { getTag(doc, it.values.toList()) } ?: listOf()

        return Html(
            title = refineTitle(title),
            pubDate = pubDate,
            thumbnail = thumbnail,
            tags = tags,
            description = description,
        )

    }
}