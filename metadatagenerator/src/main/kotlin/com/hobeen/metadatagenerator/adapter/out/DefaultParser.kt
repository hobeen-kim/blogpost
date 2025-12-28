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

        val title = getProperty(doc, parserProps.metadata.title) ?: getDefault(parserProps.props, "title-default") ?: ""
        val description = getProperty(doc, parserProps.metadata.description) ?: getDefault(parserProps.props, "description-default") ?: ""
        val thumbnail = getProperty(doc, parserProps.metadata.thumbnail) ?: getDefault(parserProps.props, "thumbnail-default")
        val pubDateStr = getProperty(doc, parserProps.metadata.pubDate)
        val pubDate = localDateParse(pubDateStr) ?: getPubDefault(parserProps.props)
        val tags = getTags(doc, parserProps.metadata.tags)
        val content = getProperty(doc, parserProps.metadata.content)

        return Html(
            title = refineTitle(title),
            pubDate = pubDate,
            thumbnail = thumbnail,
            tags = tags,
            description = description,
            content = content ?: extractMainText(doc)
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

    fun extractMainText(doc: Document): String {
        // 스크립트/스타일 제거(텍스트 노이즈 감소)
        doc.select("script, style, nav, footer, header, aside").remove()

        val candidates = doc.select("article, main, section, div")

        val best = candidates.maxByOrNull { el ->
            val text = el.text().trim()
            if (text.length < 200) return@maxByOrNull -1 // 너무 짧으면 제외

            val linkTextLen = el.select("a").text().length
            val linkRatio = if (text.isNotEmpty()) linkTextLen.toDouble() / text.length else 1.0
            val pCount = el.select("p").size

            // 점수: 텍스트 길이 + 문단 수 - 링크 비율 페널티
            (text.length + pCount * 200 - (linkRatio * 2000)).toInt()
        } ?: doc.body()

        return best.text().replace(Regex("\\s+"), " ").trim()
    }

    private fun getDefault(props: JsonNode, default: String): String? {
        val defaultValue = props[default]?.asText()

        return if(defaultValue.isNullOrBlank()) null
        else defaultValue
    }
}