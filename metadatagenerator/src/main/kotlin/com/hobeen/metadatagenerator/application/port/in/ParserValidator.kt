package com.hobeen.metadatagenerator.application.port.`in`

import com.fasterxml.jackson.databind.JsonNode
import com.hobeen.metadatagenerator.application.port.`in`.dto.HtmlResponse

interface ParserValidator {

    fun validate(url: String, props: JsonNode): HtmlResponse
}