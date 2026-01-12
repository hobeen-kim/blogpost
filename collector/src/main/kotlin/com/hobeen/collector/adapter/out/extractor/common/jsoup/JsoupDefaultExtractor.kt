package com.hobeen.collector.adapter.out.extractor.common.jsoup

import com.hobeen.blogpostcommon.util.ParseCommands
import com.hobeen.blogpostcommon.util.getDataFrom
import com.hobeen.blogpostcommon.util.localDateParse
import com.hobeen.collector.application.port.`in`.dto.MetadataNode
import com.hobeen.collector.application.port.`in`.dto.MetadataNodes
import com.hobeen.collector.domain.Message
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component

@Component
class JsoupDefaultExtractor: JsoupAbstractExtractor() {

    override fun extract(doc: Document, source: String, nodes: MetadataNodes): List<Message> {

        val posts = doc.select(nodes.list.value)

        return posts.map { post ->

            val title = getProperty(post, nodes.title) ?: ""
            val url = getProperty(post, nodes.url)
            val description = getProperty(post, nodes.description) ?: ""
            val thumbnail = getProperty(post, nodes.thumbnail)
            val pubDateStr = getProperty(post, nodes.pubDate)
            val pubDate = localDateParse(pubDateStr)
            val tags = getTags(post, nodes.tags)

            Message(
                title = title,
                source = source,
                url = url ?: throw IllegalArgumentException("url miss"),
                pubDate = pubDate,
                tags = tags,
                description = description,
                thumbnail = thumbnail,
            )
        }
    }

    private fun getProperty(doc: Element, node: List<MetadataNode>): String? {
        val commands = ParseCommands(node.map { it.toCommand() })

        val data = getDataFrom(doc, commands) ?: return null

        if(data.isEmpty()) return null

        return data[0]
    }

    private fun getTags(doc: Element, nodes: List<List<MetadataNode>>): List<String> {

        val tags = mutableListOf<String>()

        for (node in nodes) {
            val commands = ParseCommands(node.map { it.toCommand() })

            val data = getDataFrom(doc, commands)

            if(data != null) tags.addAll(data)
        }

        return tags
    }
}