package com.hobeen.collectoradapters.common.crawler

import com.fasterxml.jackson.databind.JsonNode
import com.hobeen.collectoradapters.common.fetcher.HttpFetcher
import com.hobeen.collectorengine.port.Crawler
import com.hobeen.collectorengine.port.dto.CrawlingResult

abstract class AbstractPagedHtmlCrawler(
    private val httpFetcher: HttpFetcher,
): Crawler {

    override fun crawling(url: String, props: JsonNode): CrawlingResult {

        val results = mutableListOf<String>()

        val endPage = props["end-page"]?.asInt() ?: throw IllegalArgumentException("crawling end-page is not set")
        if(endPage < 1) { throw IllegalArgumentException("crawling endPage is not valid") }

        for(page in 1..endPage) {
            val pagedUrl = getPagedUrl(url, page, props)
            val body = httpFetcher.fetch(pagedUrl)
            results.add(body)
        }

        return CrawlingResult(
            htmls = results,
        )
    }

    abstract fun getPagedUrl(url: String, page: Int, props: JsonNode): String
}