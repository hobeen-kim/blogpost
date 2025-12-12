package com.hobeen.metadatagenerator.application

import com.hobeen.metadatagenerator.application.port.out.MetadataParserSelector
import com.hobeen.metadatagenerator.domain.EnrichedMessage
import com.hobeen.metadatagenerator.domain.RawMessage
import org.springframework.stereotype.Service

@Service
class MetadataGenerateService(
    private val metadataParserSelector: MetadataParserSelector,
) {

    fun generate(rawMessage: RawMessage): EnrichedMessage {

        if(rawMessage.hasAllValues()) return rawMessage.toEnrichedMessage()
        
        //metadata parse
        val parser = metadataParserSelector.getParser(rawMessage.source)
        val html = parser.parse(rawMessage.url)

        return EnrichedMessage(
            title = rawMessage.title ?: html.title,
            source = rawMessage.source,
            url = rawMessage.url,
            pubDate = rawMessage.pubDate ?: html.pubDate,
            tags = rawMessage.tags.ifEmpty { html.tags },
            description = rawMessage.description ?: html.description,
            thumbnail = rawMessage.thumbnail ?: html.thumbnail,
        )
    }
}