package com.hobeen.metadatagenerator.adapter.out

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.blogpostcommon.util.localDateParse
import com.hobeen.metadatagenerator.application.port.out.ParseHtmlMetadataPort
import com.hobeen.metadatagenerator.common.refineTitle
import com.hobeen.metadatagenerator.domain.Html
import com.hobeen.metadatagenerator.domain.ParseProps
import org.jsoup.Jsoup
import org.springframework.stereotype.Component
import java.time.LocalDateTime

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
        val tags = parserProps.getProps("tag", objectMapper)?.let { getTag(doc, it) } ?: listOf()

        return Html(
            title = refineTitle(title),
            pubDate = pubDate ?: getPubDefault(parserProps.props),
            thumbnail = thumbnail,
            tags = tags,
            description = description,
        )
    }

    private fun getPubDefault(props: JsonNode): LocalDateTime? {
        val pubStr = props["pub-default"]?.asText()

        if(pubStr.isNullOrBlank()) return null

        if(pubStr == "now") return LocalDateTime.now()

        return localDateParse(pubStr)
    }
}