package com.hobeen.collectoradapters.source.naver.crawler

import com.fasterxml.jackson.databind.JsonNode
import com.hobeen.collectoradapters.common.fetcher.HttpFetcher
import com.hobeen.collectorengine.port.Crawler
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.springframework.stereotype.Component

@Component
class NaverCrawler(
    private val fetcher: HttpFetcher,
): Crawler {
    override fun crawling(url: String, props: JsonNode): CrawlingResult {

        val perPage = props["per-page"]?.asInt() ?: throw IllegalArgumentException("crawling per-page is not set")
        if(perPage < 1) { throw IllegalArgumentException("crawling per-page is not valid") }

        val result = fetcher.fetch("$url?size=${perPage}")

        return CrawlingResult(
            htmls = listOf(result),
        )
    }
}