package com.hobeen.collector.adapter.out.crawler.common

import com.fasterxml.jackson.databind.JsonNode
import com.hobeen.collector.application.port.`in`.dto.CrawlerProps
import com.hobeen.collector.application.port.out.Crawler
import com.hobeen.collector.application.port.out.dto.CrawlingResult
import com.hobeen.collector.adapter.out.fetcher.HttpFetcher

abstract class AbstractPagedHtmlCrawler(
    private val httpFetcher: HttpFetcher,
): Crawler {

    override fun crawling(url: String, props: CrawlerProps): CrawlingResult {

        val results = mutableListOf<String>()

        val endPage = props.properties["end-page"]?.asInt() ?: throw IllegalArgumentException("crawling end-page is not set")
        if(endPage < 1) { throw IllegalArgumentException("crawling endPage is not valid") }

        for(page in 1..endPage) {
            val pagedUrl = getPagedUrl(url, page, props.properties)
            val body = httpFetcher.fetch(pagedUrl)
            results.add(body)
        }

        return CrawlingResult(
            htmls = results,
        )
    }

    abstract fun getPagedUrl(url: String, page: Int, props: JsonNode): String
}