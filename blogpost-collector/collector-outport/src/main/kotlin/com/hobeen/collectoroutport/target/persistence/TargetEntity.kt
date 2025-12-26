package com.hobeen.collectoroutport.target.persistence

import com.fasterxml.jackson.databind.JsonNode
import com.hobeen.collectorcommon.domain.MetadataNodes
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.time.LocalDateTime

@Entity
@Table(name = "target")
data class TargetEntity (
    @Id
    val targetName: String,
    val source: String,
    val url: String,
    val crawler: String,
    val extractor: String,
    val publisher: String,
    @JdbcTypeCode(SqlTypes.JSON)
    val crawlerProps: JsonNode,
    @JdbcTypeCode(SqlTypes.JSON)
    val extractorProps: JsonNode,
    @JdbcTypeCode(SqlTypes.JSON)
    val publisherProps: JsonNode,
    @OneToMany(mappedBy = "target", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val nodes: MutableList<TargetMetadataNode> = mutableListOf(),

    var nextRunAt: LocalDateTime,
    val cron: String,
    var active: Boolean,
) {
    fun getMetadataNodes(): MetadataNodes {

        return MetadataNodes(
            list = nodes.first { it.metadataName == MetadataName.LIST }.toMetadataNode(),
            title = nodes.filter { it.metadataName == MetadataName.TITLE }.map { node -> node.toMetadataNode() },
            url = nodes.filter { it.metadataName == MetadataName.URL }.map { node -> node.toMetadataNode() },
            description = nodes.filter { it.metadataName == MetadataName.DESCRIPTION }.map { node -> node.toMetadataNode() },
            thumbnail = nodes.filter { it.metadataName == MetadataName.THUMBNAIL }.map { node -> node.toMetadataNode() },
            pubDate = nodes.filter { it.metadataName == MetadataName.PUB_DATE }.map { node -> node.toMetadataNode() },
            tags = listOf(
                nodes.filter { it.metadataName == MetadataName.TAG1 }.map { node -> node.toMetadataNode() },
                nodes.filter { it.metadataName == MetadataName.TAG2 }.map { node -> node.toMetadataNode() },
            )
        )
    }
}