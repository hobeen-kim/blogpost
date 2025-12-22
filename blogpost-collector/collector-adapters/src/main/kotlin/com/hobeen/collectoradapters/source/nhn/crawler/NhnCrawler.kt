package com.hobeen.collectoradapters.source.nhn.crawler

import com.hobeen.collectoradapters.common.fetcher.HttpFetcher
import com.hobeen.collectorengine.port.Crawler
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.springframework.stereotype.Component

@Component
class NhnCrawler(
    private val fetcher: HttpFetcher,
): Crawler {
    override fun crawling(url: String, props: Map<String, String>): CrawlingResult {

        val perPage = props["per-page"] ?: throw IllegalArgumentException("crawling per-page is not set")
        if(perPage.toIntOrNull() == null || perPage.toInt() < 1) { throw IllegalArgumentException("crawling per-page is not valid") }

        val result = fetcher.fetch("$url?rowsPerPage=${perPage}")

        return CrawlingResult(
            htmls = listOf(result),
        )
    }
}