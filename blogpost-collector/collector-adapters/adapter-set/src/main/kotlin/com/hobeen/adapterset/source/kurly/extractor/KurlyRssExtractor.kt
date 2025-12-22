package com.hobeen.adapterset.source.kurly.extractor

import com.hobeen.adaptercommon.extractor.rss.RssExtractor
import com.hobeen.collectorcommon.domain.Message
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.springframework.stereotype.Component

@Component
class KurlyRssExtractor: RssExtractor() {

    override fun extract(crawlingResult: CrawlingResult, source: String, props: Map<String, String>): List<Message> {
        val results = super.extract(crawlingResult, source, props)

        results.forEach {
            it.url = it.url.replace("http://thefarmersfront.github.io/", "https://helloworld.kurly.com/")
        }

        return results
    }
}