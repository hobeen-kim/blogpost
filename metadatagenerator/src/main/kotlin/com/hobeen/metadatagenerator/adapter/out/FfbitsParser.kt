package com.hobeen.metadatagenerator.adapter.out

import com.hobeen.metadatagenerator.application.port.out.ParseHtmlMetadataPort
import com.hobeen.metadatagenerator.domain.Html
import com.hobeen.metadatagenerator.domain.ParseProps
import org.springframework.stereotype.Component

@Component
class FfbitsParser: ParseHtmlMetadataPort {
    override fun getName(): String {
        return "ffbits"
    }

    override fun parse(url: String, parserProps: ParseProps): Html {
        return Html(
            title = "",
            pubDate = null,
            thumbnail = parserProps.props["thumbnail"]?.asText() ?: "https://podcast.44bits.io/images/44bits_large.png",
            tags = listOf(),
            description = "",
        )
    }
}