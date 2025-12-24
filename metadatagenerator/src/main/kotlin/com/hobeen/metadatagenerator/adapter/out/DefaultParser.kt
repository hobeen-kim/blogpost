package com.hobeen.metadatagenerator.adapter.out

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.blogpostcommon.util.Command
import com.hobeen.blogpostcommon.util.ParseCommand
import com.hobeen.blogpostcommon.util.ParseCommands
import com.hobeen.blogpostcommon.util.getDataFrom
import com.hobeen.blogpostcommon.util.localDateParse
import com.hobeen.metadatagenerator.application.port.out.ParseHtmlMetadataPort
import com.hobeen.metadatagenerator.common.refineTitle
import com.hobeen.metadatagenerator.domain.Html
import com.hobeen.metadatagenerator.domain.MetadataNode
import com.hobeen.metadatagenerator.domain.ParseProps
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DefaultParser: ParseHtmlMetadataPort {
    override fun getName(): String {
        return "default"
    }

    override fun parse(url: String, parserProps: ParseProps): Html {
        val doc = Jsoup.connect(url).get()

        val title = getProperty(doc, parserProps.metadata.title) ?: ""
        val description = getProperty(doc, parserProps.metadata.description) ?: ""
        val thumbnail = getProperty(doc, parserProps.metadata.thumbnail)
        val pubDateStr = getProperty(doc, parserProps.metadata.pubDate)
        val pubDate = localDateParse(pubDateStr)
        val tags = getTags(doc, parserProps.metadata.tags)

        return Html(
            title = refineTitle(title),
            pubDate = pubDate ?: getPubDefault(parserProps.props),
            thumbnail = thumbnail,
            tags = tags,
            description = description,
        )
    }

    private fun getProperty(doc: Document, node: List<MetadataNode>): String? {
        val commands = ParseCommands(node.map { it.toCommand() })

        val data = getDataFrom(doc, commands) ?: return null

        if(data.isEmpty()) return null

        return data[0]
    }

    private fun getTags(doc: Document, nodes: List<List<MetadataNode>>): List<String> {

        val tags = mutableListOf<String>()

        for (node in nodes) {
            val commands = ParseCommands(node.map { it.toCommand() })

            val data = getDataFrom(doc, commands)

            if(data != null) tags.addAll(data)
        }

        return tags
    }

    private fun getPubDefault(props: JsonNode): LocalDateTime? {
        val pubStr = props["pub-default"]?.asText()

        if(pubStr.isNullOrBlank()) return null

        if(pubStr == "now") return LocalDateTime.now()

        return localDateParse(pubStr)
    }
}