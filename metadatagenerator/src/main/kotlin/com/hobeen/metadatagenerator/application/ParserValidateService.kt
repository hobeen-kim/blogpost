package com.hobeen.metadatagenerator.application

import com.fasterxml.jackson.databind.JsonNode
import com.hobeen.metadatagenerator.application.port.`in`.ParserValidator
import com.hobeen.metadatagenerator.application.port.`in`.dto.HtmlResponse
import com.hobeen.metadatagenerator.application.port.out.MetadataParserSelector
import com.hobeen.metadatagenerator.domain.ParseProps
import org.springframework.stereotype.Component

@Component
class ParserValidateService(
    private val metadataParserSelector: MetadataParserSelector,
): ParserValidator {
    override fun validate(url: String, parserName: String, props: JsonNode): HtmlResponse {

        val parserProp = ParseProps(
            source = "test",
            parser = parserName,
            props = props
        )

        val parser = metadataParserSelector.getParser(parserProp.parser)
        val html = parser.parse(url, parserProp)

        return HtmlResponse.of(html)
    }
}