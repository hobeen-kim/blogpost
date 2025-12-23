package com.hobeen.metadatagenerator.adapter.out

import com.fasterxml.jackson.databind.JsonNode
import com.hobeen.blogpostcommon.util.localDateParse
import com.hobeen.metadatagenerator.application.port.out.ParseHtmlMetadataPort
import com.hobeen.metadatagenerator.domain.Html
import com.hobeen.metadatagenerator.domain.ParseProps
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class NoneParser: ParseHtmlMetadataPort {
    override fun getName(): String {
        return "none"
    }

    override fun parse(
        url: String,
        parserProps: ParseProps
    ): Html {
        return Html(
            title = getDefault(parserProps.props, "title-default") ?: "",
            pubDate = getPubDefault(parserProps.props),
            thumbnail = getDefault(parserProps.props, "thumbnail-default"),
            tags = listOf(),
            description = getDefault(parserProps.props, "description-default") ?: "",
        )
    }

    private fun getDefault(props: JsonNode, default: String): String? {
        val defaultValue = props[default]?.asText()

        return if(defaultValue.isNullOrBlank()) null
        else defaultValue
    }

    private fun getPubDefault(props: JsonNode): LocalDateTime? {
        val pubStr = props["pub-default"]?.asText()

        if(pubStr.isNullOrBlank()) return null

        if(pubStr == "now") return LocalDateTime.now()

        return localDateParse(pubStr)
    }

}