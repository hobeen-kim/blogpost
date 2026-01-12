package com.hobeen.collector.adapter.out.crawler.common

import com.hobeen.collector.application.port.`in`.dto.CrawlerProps
import com.hobeen.collector.application.port.out.Crawler
import com.hobeen.collector.application.port.out.dto.CrawlingResult
import com.hobeen.collector.adapter.out.fetcher.HttpFetcher
import org.springframework.stereotype.Component

@Component
class HtmlCrawler(
    private val httpFetcher: HttpFetcher,
): Crawler {
    override fun crawling(url: String, props: CrawlerProps): CrawlingResult {
        val body = httpFetcher.fetch(url)

        return CrawlingResult(
            htmls = listOf(body),
        )

    }
}