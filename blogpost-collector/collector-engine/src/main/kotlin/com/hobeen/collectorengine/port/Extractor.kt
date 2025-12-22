package com.hobeen.collectorengine.port

import com.hobeen.collectorengine.port.dto.CrawlingResult
import com.hobeen.collectorcommon.domain.Message


interface Extractor {
    fun extract(crawlingResult: CrawlingResult, source: String, props: Map<String, String>): List<Message>
}
