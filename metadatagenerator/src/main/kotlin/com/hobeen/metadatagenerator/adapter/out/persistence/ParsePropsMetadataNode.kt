package com.hobeen.metadatagenerator.adapter.out.persistence

import com.hobeen.blogpostcommon.util.Command
import com.hobeen.metadatagenerator.domain.MetadataNode
import jakarta.persistence.*

@Entity
@Table(name = "metadata_node")
class ParsePropsMetadataNode (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val nodeId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source")
    var parseProps: ParsePropsEntity,

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
    TITLE, DESCRIPTION, THUMBNAIL, PUB_DATE, TAG1, TAG2, CONTENT
}