package com.hobeen.collector.adapter.out.extractor.source.line

import com.hobeen.collector.adapter.out.extractor.common.rss.RssExtractor
import com.hobeen.collector.application.port.`in`.dto.ExtractorProps
import com.hobeen.collector.application.port.out.dto.CrawlingResult
import com.hobeen.collector.domain.Message
import org.springframework.stereotype.Component
import kotlin.collections.forEach
import kotlin.collections.last
import kotlin.text.split

@Component
class LineRssExtractor: RssExtractor() {

    override fun extract(crawlingResult: CrawlingResult, source: String, props: ExtractorProps): List<Message> {
        val results = super.extract(crawlingResult, source, props)

        results.forEach {
            if(it.description != null) {
                it.description = it.description!!.split("기사를 번역한 글입니다.").last()
            }
        }

        return results
    }
}