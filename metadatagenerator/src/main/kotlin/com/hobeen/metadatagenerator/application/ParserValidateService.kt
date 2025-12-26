package com.hobeen.metadatagenerator.application

import com.fasterxml.jackson.databind.JsonNode
import com.hobeen.metadatagenerator.application.port.`in`.ParserValidator
import com.hobeen.metadatagenerator.application.port.`in`.dto.HtmlResponse
import com.hobeen.metadatagenerator.application.port.out.GetParsePropPort
import com.hobeen.metadatagenerator.application.port.out.MetadataParserSelector
import com.hobeen.metadatagenerator.domain.MetadataNodes
import com.hobeen.metadatagenerator.domain.ParseProps
import org.springframework.stereotype.Component

@Component
class ParserValidateService(
    private val metadataParserSelector: MetadataParserSelector,
    private val getParsePropPort: GetParsePropPort,
): ParserValidator {
    override fun validate(url: String, parserName: String, props: JsonNode, metadata: MetadataNodes): HtmlResponse {

        val parserProp = ParseProps(
            source = "test",
            parser = parserName,
            props = props,
            metadata = metadata
        )

        val parser = metadataParserSelector.getParser(parserProp.parser)
        val html = parser.parse(url, parserProp)

        return HtmlResponse.of(html)
    }

    override fun validate(
        url: String,
        source: String
    ): HtmlResponse {

        val parserProp = getParsePropPort.getParseProp(source)
        val parser = metadataParserSelector.getParser(parserProp.parser)
        val html = parser.parse(url, parserProp)

        return HtmlResponse.of(html)
    }
}