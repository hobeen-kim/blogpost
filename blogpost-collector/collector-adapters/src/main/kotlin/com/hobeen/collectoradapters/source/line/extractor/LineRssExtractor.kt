package com.hobeen.collectoradapters.source.line.extractor

import com.hobeen.collectoradapters.common.extractor.rss.RssExtractor
import com.hobeen.collectorcommon.domain.Message
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.springframework.stereotype.Component

@Component
class LineRssExtractor: RssExtractor() {

    override fun extract(crawlingResult: CrawlingResult, source: String, props: Map<String, String>): List<Message> {
        val results = super.extract(crawlingResult, source, props)

        results.forEach {
            if(it.description != null) {
                it.description = it.description!!.split("기사를 번역한 글입니다.").last()
            }
        }

        return results
    }
}