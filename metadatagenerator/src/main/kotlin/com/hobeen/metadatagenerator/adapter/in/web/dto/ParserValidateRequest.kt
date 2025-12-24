package com.hobeen.metadatagenerator.adapter.`in`.web.dto

import com.fasterxml.jackson.databind.JsonNode
import com.hobeen.metadatagenerator.domain.MetadataNodes

data class ParserValidateRequest (
    val url: String,
    val parser: String,
    val props: JsonNode,
    val metadata: MetadataNodes
)