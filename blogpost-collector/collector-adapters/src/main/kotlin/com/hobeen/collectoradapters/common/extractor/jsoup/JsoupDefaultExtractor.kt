package com.hobeen.collectoradapters.common.extractor.jsoup

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.collectorcommon.domain.Message
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class JsoupDefaultExtractor(
    private val objectMapper: ObjectMapper,
): JsoupAbstractExtractor() {

    override fun extract(doc: Document, source: String, props: JsonNode): List<Message> {

        val listQuery = props["list-query"]?.asText()

        val posts = doc.select(listQuery)

        return posts.map { post ->
            Message(
                title = getProps(props, "title-query")?.let { getDataFrom(post, it) },
                source = source,
                url = getProps(props, "url-query")?.let { getDataFrom(post, it) } ?: throw IllegalArgumentException("url miss"),
                pubDate = getProps(props, "pub-query")?.let { getDataFrom(post, it) }?.let { getPubDate(it) },
                tags = getProps(props, "tag-query")?.let {getTag(doc, it) } ?: listOf(),
                description = getProps(props, "description-query")?.let { getDataFrom(post, it) },
                thumbnail = getProps(props, "thumbnail-query")?.let { getDataFrom(post, it) },
            )
        }
    }

    private fun getProps(props: JsonNode, key: String): Map<String, String>? {
        val propNode = props[key] ?: return null

        return objectMapper.convertValue(propNode, object : TypeReference<Map<String, String>>() {})
    }

    private fun getDataFrom(doc: Element, map: Map<String, String>): String? {

        var element: Element? = doc
        var result: String? = null

        map.entries.sortedBy { entry -> sort(entry.key) }.forEach { entry ->

            when(entry.key) {
                "selectFirst" -> element = doc.selectFirst(entry.value)
                "attr" -> result = element?.attr(entry.value)
                "text" -> result = element?.text()
                "delete1" -> result = result?.replace(entry.value, "")
                "delete2" -> result = result?.replace(entry.value, "")
                "prefix" -> result = if(result == null) null else entry.value + result
            }

            if(element == null) return@forEach
        }

        return result
    }

    private fun sort(key: String): Int {
        return when(key) {
            "selectFirst" -> 1
            "attr" -> 2
            "text" -> 3
            "delete1" -> 4
            "delete2" -> 5
            "prefix" -> 6
            else -> 7
        }
    }

    private fun getTag(doc: Element, map: Map<String, String>): List<String> {

        val tags = mutableListOf<String>()

        map.entries.forEach { entry ->
            tags.addAll(doc.select(entry.value).map { it.text() })
        }

        return tags
    }

    private fun getPubDate(dateStr: String): LocalDateTime? {
        val snippets = dateStr.split("-")

        if(snippets.size != 3) return null

        val year = snippets[0].toInt()
        val month = snippets[1].toInt()
        val day = snippets[2].toInt()

        return LocalDateTime.of(year, month, day, 0, 0, 0)
    }
}