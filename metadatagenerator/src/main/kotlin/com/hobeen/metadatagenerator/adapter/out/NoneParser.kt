package com.hobeen.metadatagenerator.adapter.out

import com.hobeen.metadatagenerator.application.port.out.ParseHtmlMetadataPort
import com.hobeen.metadatagenerator.domain.Html
import com.hobeen.metadatagenerator.domain.ParseProps
import org.springframework.stereotype.Component

@Component
class NonParser: ParseHtmlMetadataPort {
    override fun getName(): String {
        return "none"
    }

    override fun parse(
        url: String,
        parserProps: ParseProps
    ): Html {
        return Html(
            title = "",
            pubDate = null,
            thumbnail = "",
            tags = listOf(),
            description = "",
        )
    }
}