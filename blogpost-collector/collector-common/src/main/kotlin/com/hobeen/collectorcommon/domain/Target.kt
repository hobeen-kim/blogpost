package com.hobeen.collectorcommon.domain

import com.fasterxml.jackson.databind.JsonNode
import com.hobeen.blogpostcommon.util.Command
import com.hobeen.blogpostcommon.util.ParseCommand

data class Target (
    val url: String,
    val source: String,
    val adapter: AdapterProps,
)

data class AdapterProps(
    val crawler: CrawlerProps,
    val extractor: ExtractorProps,
    val publisher: PublisherProps,
)

data class CrawlerProps(
    val type: String = "htmlCrawler",
    val properties: JsonNode,
)

data class ExtractorProps(
    val type: String = "rssExtractor",
    val properties: JsonNode,
    val metadata: MetadataNodes,
)

data class PublisherProps(
    val type: String = "kafkaPublisher",
    val properties: JsonNode
)

data class MetadataNodes(
    val list: MetadataNode,
    val title: List<MetadataNode>,
    val url: List<MetadataNode>,
    val description: List<MetadataNode>,
    val thumbnail: List<MetadataNode>,
    val pubDate: List<MetadataNode>,
    val tags: List<List<MetadataNode>>,
) {
    companion object {
        val EMPTY = MetadataNodes(
            list = MetadataNode(0, Command.SELECT, "empty"),
            title = listOf(),
            url = listOf(),
            description = listOf(),
            thumbnail = listOf(),
            pubDate = listOf(),
            tags = listOf(),
        )
    }
}

data class MetadataNode(
    val order: Int,
    val command: Command,
    val value: String,
) {
    fun toCommand(): ParseCommand {
        return ParseCommand(
            order = order,
            command = command,
            value = value,
        )
    }
}