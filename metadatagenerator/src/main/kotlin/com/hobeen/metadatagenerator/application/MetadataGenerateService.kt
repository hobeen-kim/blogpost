package com.hobeen.metadatagenerator.application

import com.hobeen.blogpostcommon.exception.InSufficientMetadataException
import com.hobeen.metadatagenerator.application.port.`in`.MetadataGenerator
import com.hobeen.metadatagenerator.application.port.out.ContentAbstractPort
import com.hobeen.metadatagenerator.application.port.out.GetParsePropPort
import com.hobeen.metadatagenerator.application.port.out.MetadataParserSelector
import com.hobeen.metadatagenerator.application.port.out.SaveMessagePort
import com.hobeen.metadatagenerator.application.port.out.TagExtractPort
import com.hobeen.blogpostcommon.alarm.AlarmDto
import com.hobeen.blogpostcommon.alarm.AlarmService
import com.hobeen.metadatagenerator.domain.EnrichedMessage
import com.hobeen.metadatagenerator.domain.Html
import com.hobeen.metadatagenerator.domain.RawMessage
import com.hobeen.metadatagenerator.domain.TagInfo
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class MetadataGenerateService(
    private val metadataParserSelector: MetadataParserSelector,
    private val saveMessagePort: SaveMessagePort,
    private val getParsePropPort: GetParsePropPort,
    private val contentAbstractPort: ContentAbstractPort,
    private val tagExtractPort: TagExtractPort,
    private val alarmService: AlarmService,
): MetadataGenerator {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun generate(message: RawMessage): EnrichedMessage {

        //metadata parse
        val html = parseFromHtmlContent(message)

        //all Data with message or html
        val title = if(message.title.isNullOrBlank()) html.title else message.title
        val source = message.source
        val url = message.url
        val pubDate = getPubDateOrThrow(message, html)
        val tags = (message.tags + html.tags).distinct()
        val description = if(message.description.isNullOrBlank()) html.description else message.description
        val thumbnail = getThumbnailOrThrow(message, html)
        val content = html.content
        val abstractedContent = contentAbstractPort.abstract(title, description, tags, content)

        // tag extraction via taggenerator
        val enrichedTags = try {
            val tagResult = tagExtractPort.extractTags(title, tags, content, abstractedContent)
            val baseTags = tags.map { TagInfo(name = it) }
            val level1Tag = listOf(TagInfo(name = tagResult.level1, level = 1))
            val level2Tags = (tagResult.level2Selected + tagResult.level2New).map { TagInfo(name = it, level = 2) }
            val level3Tags = (tagResult.level3Selected + tagResult.level3New).map { TagInfo(name = it, level = 3) }
            (level1Tag + level2Tags + level3Tags + baseTags).distinctBy { it.name }
        } catch (e: Exception) {
            log.warn("TagGenerator 호출 실패, 기존 태그 유지: ${e.message}")
            alarmService.sendAlarm(AlarmDto(
                alarmMsg = "MetadataGenerator:TagGenerator 호출 실패",
                source = source,
                url = url,
                rawData = "title=$title, tags=$tags",
                exception = e,
                exceptionPrintStackDepth = 2,
            ))
            tags.map { TagInfo(name = it) }
        }

        return EnrichedMessage(
            title = title,
            source = source,
            url = url,
            pubDate = pubDate,
            tags = enrichedTags,
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