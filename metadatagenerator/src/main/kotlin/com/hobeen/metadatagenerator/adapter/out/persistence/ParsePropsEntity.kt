package com.hobeen.metadatagenerator.adapter.out.persistence

import com.fasterxml.jackson.databind.JsonNode
import com.hobeen.metadatagenerator.domain.ParseProps
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "parse_props")
data class ParsePropsEntity (
    @Id
    val source: String,
    val parser: String,
    @JdbcTypeCode(SqlTypes.JSON)
    val props: JsonNode,
) {
    fun toParserProps(): ParseProps {
        return ParseProps(
            source = source,
            parser = parser,
            props = props,
        )
    }
}