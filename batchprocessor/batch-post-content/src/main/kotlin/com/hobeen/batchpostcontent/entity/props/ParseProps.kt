package com.hobeen.batchpostcontent.entity.props

import com.fasterxml.jackson.databind.JsonNode
import com.hobeen.blogpostcommon.util.ParseCommand
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
class ParseProps (
    @Id
    val source: String,
    val parser: String,
    @OneToMany(mappedBy = "parseProps", fetch = FetchType.LAZY, cascade = [CascadeType.ALL], orphanRemoval = true)
    val nodes: MutableList<ParsePropsMetadataNode> = mutableListOf()
) {
    fun getContentNode(): List<ParseCommand> {
        return nodes.filter { it.metadataName == MetadataName.CONTENT }.map {
            ParseCommand(
                order = it.order,
                command = it.command,
                value = it.value
            )
        }
    }
}
