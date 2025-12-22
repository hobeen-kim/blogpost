package com.hobeen.collectoradapters.common.crawler

import com.hobeen.collectoradapters.common.fetcher.HttpFetcher
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