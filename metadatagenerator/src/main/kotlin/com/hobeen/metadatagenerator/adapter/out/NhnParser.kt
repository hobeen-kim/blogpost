package com.hobeen.metadatagenerator.adapter.out

import com.hobeen.metadatagenerator.application.port.out.ParseHtmlMetadataPort
import com.hobeen.metadatagenerator.domain.Html
import com.hobeen.metadatagenerator.domain.ParseProps
import org.springframework.stereotype.Component

@Component
class NhnParser: ParseHtmlMetadataPort {
    override fun getName(): String {
        return "nhn"
    }

    override fun parse(url: String, parserProps: ParseProps): Html {
        return Html(
            title = "",
            pubDate = null,
            thumbnail = parserProps.props["thumbnail"]?.asText() ?: "DEFAULT",
            tags = listOf(),
            description = "",
        )
    }
}