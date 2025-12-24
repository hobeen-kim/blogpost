package com.hobeen.metadatagenerator.domain

import com.fasterxml.jackson.databind.JsonNode
import com.hobeen.blogpostcommon.util.Command
import com.hobeen.blogpostcommon.util.ParseCommand

data class ParseProps (
    val source: String,
    val parser: String,
    val props: JsonNode,
    val metadata: MetadataNodes,
)

data class MetadataNodes(
    val title: List<MetadataNode>,
    val description: List<MetadataNode>,
    val thumbnail: List<MetadataNode>,
    val pubDate: List<MetadataNode>,
    val tags: List<List<MetadataNode>>,
)

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