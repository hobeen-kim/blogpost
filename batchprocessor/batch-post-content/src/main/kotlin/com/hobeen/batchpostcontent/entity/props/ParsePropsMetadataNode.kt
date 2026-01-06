package com.hobeen.batchpostcontent.entity.props

import com.hobeen.blogpostcommon.util.Command
import jakarta.persistence.*

@Entity
@Table(name = "metadata_node")
class ParsePropsMetadataNode (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val nodeId: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source")
    var parseProps: ParseProps,

    val order: Int,
    @Enumerated(EnumType.STRING)
    val metadataName: MetadataName,
    @Enumerated(EnumType.STRING)
    val command: Command,
    val value: String,
)

enum class MetadataName {
    TITLE, DESCRIPTION, THUMBNAIL, PUB_DATE, TAG1, TAG2, CONTENT
}
