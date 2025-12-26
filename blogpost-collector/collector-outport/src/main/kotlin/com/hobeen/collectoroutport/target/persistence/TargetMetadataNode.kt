package com.hobeen.collectoroutport.target.persistence

import com.hobeen.blogpostcommon.util.Command
import com.hobeen.collectorcommon.domain.MetadataNode
import jakarta.persistence.*

@Entity
@Table(name = "target_extractor_metadata_node")
class TargetMetadataNode (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val nodeId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_name")
    var target: TargetEntity,

    val order: Int,
    @Enumerated(EnumType.STRING)
    val metadataName: MetadataName,
    @Enumerated(EnumType.STRING)
    val command: Command,
    val value: String,
) {
    fun toMetadataNode(): MetadataNode {
        return MetadataNode(
            order = order,
            command = command,
            value = value
        )
    }
}

enum class MetadataName {
    LIST, TITLE, URL, DESCRIPTION, THUMBNAIL, PUB_DATE, TAG1, TAG2,
}