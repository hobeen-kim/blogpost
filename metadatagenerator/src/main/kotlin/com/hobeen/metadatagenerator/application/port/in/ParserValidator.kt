package com.hobeen.metadatagenerator.application.port.`in`

import com.fasterxml.jackson.databind.JsonNode
import com.hobeen.metadatagenerator.application.port.`in`.dto.HtmlResponse
import com.hobeen.metadatagenerator.domain.MetadataNodes

interface ParserValidator {

    fun validate(url: String, parserName: String, props: JsonNode, metadata: MetadataNodes): HtmlResponse
}