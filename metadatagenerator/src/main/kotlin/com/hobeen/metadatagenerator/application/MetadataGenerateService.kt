package com.hobeen.metadatagenerator.application

import com.hobeen.metadatagenerator.application.port.`in`.MetadataGenerator
import com.hobeen.metadatagenerator.application.port.out.MetadataParserSelector
import com.hobeen.metadatagenerator.application.port.out.SaveMessagePort
import com.hobeen.metadatagenerator.domain.EnrichedMessage
import com.hobeen.metadatagenerator.domain.RawMessage
import org.springframework.stereotype.Service

@Service
class MetadataGenerateService(
    private val metadataParserSelector: MetadataParserSelector,
    private val saveMessagePort: SaveMessagePort,
): MetadataGenerator {

    override fun generate(message: RawMessage): EnrichedMessage {

        if(message.hasAllValues()) return message.toEnrichedMessage()
        
        //metadata parse
        val parser = metadataParserSelector.getParser(message.source)
        val html = parser.parse(message.url)

        return EnrichedMessage(
            title = message.title ?: html.title,
            source = message.source,
            url = message.url,
            pubDate = message.pubDate ?: html.pubDate,
            tags = message.tags.ifEmpty { html.tags },
            description = message.description ?: html.description,
            thumbnail = message.thumbnail ?: html.thumbnail,
        )
    }

    override fun save(message: EnrichedMessage) {
        saveMessagePort.save(message)
    }
}