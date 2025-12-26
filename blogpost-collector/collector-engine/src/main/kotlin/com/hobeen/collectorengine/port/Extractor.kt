package com.hobeen.collectorengine.port

import com.fasterxml.jackson.databind.JsonNode
import com.hobeen.collectorcommon.domain.ExtractorProps
import com.hobeen.collectorengine.port.dto.CrawlingResult
import com.hobeen.collectorcommon.domain.Message


interface Extractor {
    fun extract(crawlingResult: CrawlingResult, source: String, props: ExtractorProps): List<Message>
}
