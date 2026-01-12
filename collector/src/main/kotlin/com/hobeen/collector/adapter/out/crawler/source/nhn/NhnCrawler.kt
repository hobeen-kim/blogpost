package com.hobeen.collector.adapter.out.crawler.source.nhn

import com.hobeen.collector.adapter.out.fetcher.HttpFetcher
import com.hobeen.collector.application.port.`in`.dto.CrawlerProps
import com.hobeen.collector.application.port.out.Crawler
import com.hobeen.collector.application.port.out.dto.CrawlingResult
import org.springframework.stereotype.Component
import kotlin.text.get

@Component
class NhnCrawler(
    private val fetcher: HttpFetcher,
): Crawler {
    override fun crawling(url: String, props: CrawlerProps): CrawlingResult {

        val perPage = props.properties["per-page"]?.asInt() ?: throw IllegalArgumentException("crawling per-page is not set")
        if(perPage < 1) { throw IllegalArgumentException("crawling per-page is not valid") }

        val result = fetcher.fetch("$url?rowsPerPage=${perPage}")

        return CrawlingResult(
            htmls = listOf(result),
        )
    }
}