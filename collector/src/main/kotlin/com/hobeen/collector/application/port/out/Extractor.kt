package com.hobeen.collector.application.port.out

import com.hobeen.collector.application.port.`in`.dto.ExtractorProps
import com.hobeen.collector.application.port.out.dto.CrawlingResult
import com.hobeen.collector.domain.Message


interface Extractor {
    fun extract(crawlingResult: CrawlingResult, source: String, props: ExtractorProps): List<Message>
}
