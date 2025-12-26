package com.hobeen.collectoradapters.common.crawler

import com.fasterxml.jackson.databind.JsonNode
import com.hobeen.collectoradapters.common.fetcher.HttpFetcher
import com.hobeen.collectorcommon.domain.CrawlerProps
import com.hobeen.collectorengine.port.Crawler
import com.hobeen.collectorengine.port.dto.CrawlingResult
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