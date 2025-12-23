package com.hobeen.metadatagenerator.application.port.out

import com.hobeen.blogpostcommon.exception.ParserMissingException
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class MetadataParserSelector (
    private val adapters: List<ParseHtmlMetadataPort>,
) {

    @PostConstruct
    fun check() {
        adapters.find { it.getName() == "default" } ?: throw IllegalArgumentException("default adapter does not exist")
    }

    private val adapterMap = adapters.associateBy { it.getName() }

    fun getParser(parserName: String): ParseHtmlMetadataPort {

        return adapterMap[parserName] ?: throw ParserMissingException("parserName")
    }
}