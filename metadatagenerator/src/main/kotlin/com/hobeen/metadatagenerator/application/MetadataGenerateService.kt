package com.hobeen.metadatagenerator.application

import com.hobeen.blogpostcommon.exception.InSufficientMetadataException
import com.hobeen.metadatagenerator.application.port.`in`.MetadataGenerator
import com.hobeen.metadatagenerator.application.port.out.MetadataParserSelector
import com.hobeen.metadatagenerator.application.port.out.SaveMessagePort
import com.hobeen.metadatagenerator.domain.EnrichedMessage
import com.hobeen.metadatagenerator.domain.Html
import com.hobeen.metadatagenerator.domain.RawMessage
import org.springframework.stereotype.Service
import java.time.LocalDateTime

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
            title = if(message.title.isNullOrBlank()) html.title else message.title,
            source = message.source,
            url = message.url,
            pubDate = getPubDateOrThrow(message, html),
            tags = message.tags.ifEmpty { html.tags },
            description = if(message.description.isNullOrBlank()) html.description else message.description,
            thumbnail = getThumbnailOrThrow(message, html),
        )
    }

    override fun save(message: EnrichedMessage) {
        saveMessagePort.save(message)
    }

    private fun getPubDateOrThrow(message: RawMessage, html: Html): LocalDateTime {

        return message.pubDate ?: (html.pubDate ?: throw InSufficientMetadataException("pubDate"))
    }

    private fun getThumbnailOrThrow(message: RawMessage, html: Html): String {
        return if(!message.thumbnail.isNullOrBlank()) message.thumbnail
        else html.thumbnail ?: throw InSufficientMetadataException("thumbnail")
    }
}