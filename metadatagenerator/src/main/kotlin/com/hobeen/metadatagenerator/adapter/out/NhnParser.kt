package com.hobeen.metadatagenerator.adapter.out

import com.hobeen.metadatagenerator.application.port.out.ParseHtmlMetadataPort
import com.hobeen.metadatagenerator.domain.Html
import org.springframework.stereotype.Component

@Component
class NhnParser: ParseHtmlMetadataPort {
    override fun getName(): String {
        return "nhn"
    }

    override fun parse(url: String): Html {
        return Html(
            title = "",
            pubDate = null,
            thumbnail = "DEFAULT",
            tags = listOf(),
            description = "",
        )
    }
}