package com.hobeen.adaptercommon.crawler

import com.hobeen.adaptercommon.fetcher.HttpFetcher
import com.hobeen.collectorengine.port.Crawler
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.springframework.stereotype.Component

@Component
class HtmlCrawler(
    private val httpFetcher: HttpFetcher,
): Crawler {
    override fun crawling(url: String, props: Map<String, String>): CrawlingResult {
        val body = httpFetcher.fetch(url)

        return CrawlingResult(
            htmls = listOf(body),
        )

    }
}