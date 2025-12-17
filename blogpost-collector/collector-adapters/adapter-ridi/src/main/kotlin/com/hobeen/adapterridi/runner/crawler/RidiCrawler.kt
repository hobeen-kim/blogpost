package com.hobeen.adapterridi.runner.crawler

import com.hobeen.adaptercommon.fetcher.HttpFetcher
import com.hobeen.adapterridi.runner.config.RidiProperties
import com.hobeen.collectorengine.port.Crawler
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.springframework.stereotype.Component

@Component
class RidiCrawler(
    private val fetcher: HttpFetcher,
    private val ridiProperties: RidiProperties,
): Crawler {
    override fun crawling(url: String): CrawlingResult {
        val results = mutableListOf<String>()

        //첫 페이지는 그대로
        results.add(fetcher.fetch(url))

        for(page in 2..ridiProperties.endPage) {
            val htmlString = fetcher.fetch(url + "page/$page/")
            results.add(htmlString)
        }

        return CrawlingResult(results)
    }
}