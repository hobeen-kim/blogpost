package com.hobeen.adapterkurly.extractor

import com.hobeen.adaptercommon.extractor.rss.RssExtractor
import com.hobeen.collectorcommon.domain.Message
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.springframework.stereotype.Component

@Component
class KurlyRssExtractor: RssExtractor() {

    override fun extract(crawlingResult: CrawlingResult, source: String): List<Message> {
        val results = super.extract(crawlingResult, source)

        results.forEach {
            it.url = it.url.replace("http://thefarmersfront.github.io/", "https://helloworld.kurly.com/")
        }

        return results
    }
}