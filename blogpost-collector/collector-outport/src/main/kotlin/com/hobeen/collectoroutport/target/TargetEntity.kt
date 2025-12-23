package com.hobeen.collectoroutport.target

import com.fasterxml.jackson.databind.JsonNode
import jakarta.persistence.Entity
import jakarta.persistence.Id
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
    @JdbcTypeCode(SqlTypes.JSON)
    val crawler: JsonNode,
    @JdbcTypeCode(SqlTypes.JSON)
    val extractor: JsonNode,
    @JdbcTypeCode(SqlTypes.JSON)
    val publisher: JsonNode,

    var nextRunAt: LocalDateTime,
    val cron: String,
    var active: Boolean,
)