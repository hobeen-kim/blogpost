package com.hobeen.adapterridi.runner.crawler

import com.hobeen.adaptercommon.fetcher.HttpFetcher
import com.hobeen.collectorengine.port.Crawler
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.springframework.stereotype.Component

@Component
class RidiCrawler(
    private val fetcher: HttpFetcher,
): Crawler {
    override fun crawling(url: String): CrawlingResult {
        val results = mutableListOf<String>()

        //첫 페이지는 그대로
        results.add(fetcher.fetch(url))

        //2~9 페이지는 page 번호로 read
        for(page in 2..9) {
            val htmlString = fetcher.fetch(url + "page/$page/")
            results.add(htmlString)
        }

        return CrawlingResult(results)
    }
}