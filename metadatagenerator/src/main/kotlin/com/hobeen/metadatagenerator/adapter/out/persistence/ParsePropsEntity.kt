package com.hobeen.metadatagenerator.adapter.out.persistence

import com.fasterxml.jackson.databind.JsonNode
import com.hobeen.blogpostcommon.util.Command
import com.hobeen.metadatagenerator.domain.MetadataNode
import com.hobeen.metadatagenerator.domain.MetadataNodes
import com.hobeen.metadatagenerator.domain.ParseProps
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "parse_props")
class ParsePropsEntity (
    @Id
    val source: String,
    val parser: String,
    @JdbcTypeCode(SqlTypes.JSON)
    val props: JsonNode,
    @OneToMany(mappedBy = "parseProps", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val nodes: MutableList<ParsePropsMetadataNode> = mutableListOf()
) {
    fun toParserProps(): ParseProps {
        return ParseProps(
            source = source,
            parser = parser,
            props = props,
            metadata = MetadataNodes(
                title = nodes.filter { it.metadataName == MetadataName.TITLE }.map { node -> node.toMetadataNode() },
                description = nodes.filter { it.metadataName == MetadataName.DESCRIPTION }.map { node -> node.toMetadataNode() },
                thumbnail = nodes.filter { it.metadataName == MetadataName.THUMBNAIL }.map { node -> node.toMetadataNode() },
                pubDate = nodes.filter { it.metadataName == MetadataName.PUB_DATE }.map { node -> node.toMetadataNode() },
                tags = listOf(
                    nodes.filter { it.metadataName == MetadataName.TAG1 }.map { node -> node.toMetadataNode() },
                    nodes.filter { it.metadataName == MetadataName.TAG2 }.map { node -> node.toMetadataNode() },
                ),
                content = nodes.filter { it.metadataName == MetadataName.CONTENT }.map { node -> node.toMetadataNode() },
            )
        )
    }
}