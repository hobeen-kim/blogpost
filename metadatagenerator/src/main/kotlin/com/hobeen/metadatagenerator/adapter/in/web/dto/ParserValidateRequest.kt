package com.hobeen.metadatagenerator.adapter.`in`.web.dto

import com.fasterxml.jackson.databind.JsonNode

data class ParserValidateRequest (
    val url: String,
    val props: JsonNode,
)