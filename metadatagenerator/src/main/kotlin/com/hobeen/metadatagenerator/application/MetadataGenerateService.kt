package com.hobeen.metadatagenerator.application

import com.hobeen.blogpostcommon.exception.InSufficientMetadataException
import com.hobeen.metadatagenerator.application.port.`in`.MetadataGenerator
import com.hobeen.metadatagenerator.application.port.out.ContentAbstractPort
import com.hobeen.metadatagenerator.application.port.out.GetParsePropPort
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
    private val getParsePropPort: GetParsePropPort,
    private val contentAbstractPort: ContentAbstractPort,
): MetadataGenerator {

    override fun generate(message: RawMessage): EnrichedMessage {

        //metadata parse
        val html = parseFromHtmlContent(message)

        //all Data with message or html
        val title = if(message.title.isNullOrBlank()) html.title else message.title
        val source = message.source
        val url = message.url
        val pubDate = getPubDateOrThrow(message, html)
        val tags = message.tags.ifEmpty { html.tags }
        val description = if(message.description.isNullOrBlank()) html.description else message.description
        val thumbnail = getThumbnailOrThrow(message, html)
        val content = html.content
        val abstractedContent = contentAbstractPort.abstract(title, description, tags, content)

        return EnrichedMessage(
            title = title,
            source = source,
            url = url,
            pubDate = pubDate,
            tags = tags,
            description = description,
            thumbnail = thumbnail,
            content = content,
            abstractedContent = abstractedContent,
        )
    }

    override fun save(message: EnrichedMessage) {
        saveMessagePort.save(message)
    }

    private fun parseFromHtmlContent(message: RawMessage): Html {
        val parserProp = getParsePropPort.getParseProp(message.source)
        val parser = metadataParserSelector.getParser(parserProp.parser)
        val html = parser.parse(message.url, parserProp)
        return html
    }

    private fun getPubDateOrThrow(message: RawMessage, html: Html): LocalDateTime {

        return message.pubDate ?: (html.pubDate ?: throw InSufficientMetadataException("pubDate"))
    }

    private fun getThumbnailOrThrow(message: RawMessage, html: Html): String {
        return if(!message.thumbnail.isNullOrBlank()) message.thumbnail
        else html.thumbnail ?: throw InSufficientMetadataException("thumbnail")
    }
}