package com.hobeen.metadatagenerator.adapter.out

import com.hobeen.metadatagenerator.application.port.out.ParseHtmlMetadataPort
import com.hobeen.metadatagenerator.domain.Html
import org.springframework.stereotype.Component

@Component
class DefaultParser: ParseHtmlMetadataPort {
    override fun getName(): String {
        return "default"
    }

    override fun parse(url: String): Html {
        TODO("Not yet implemented")
    }
}