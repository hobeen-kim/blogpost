package com.hobeen.collectoradapters.common.extractor.jsoup

import com.fasterxml.jackson.databind.JsonNode
import com.hobeen.collectorcommon.domain.ExtractorProps
import com.hobeen.collectorcommon.domain.Message
import com.hobeen.collectorcommon.domain.MetadataNodes
import com.hobeen.collectorengine.port.Extractor
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

abstract class JsoupAbstractExtractor: Extractor {

    override fun extract(crawlingResult: CrawlingResult, source: String, props: ExtractorProps): List<Message> {
        return crawlingResult.htmls.flatMap { html ->
            val doc: Document = Jsoup.parse(html)
            extract(doc, source, props.metadata)
        }
    }

    abstract fun extract(doc: Document, source: String, metadataNodes: MetadataNodes): List<Message>
}