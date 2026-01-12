package com.hobeen.collector.adapter.out.extractor.common.jsoup

import com.hobeen.collector.application.port.`in`.dto.ExtractorProps
import com.hobeen.collector.application.port.`in`.dto.MetadataNodes
import com.hobeen.collector.application.port.out.Extractor
import com.hobeen.collector.application.port.out.dto.CrawlingResult
import com.hobeen.collector.domain.Message
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