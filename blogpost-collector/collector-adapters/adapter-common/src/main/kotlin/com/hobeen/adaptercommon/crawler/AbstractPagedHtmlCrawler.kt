package com.hobeen.adaptercommon.crawler

import com.hobeen.adaptercommon.fetcher.HttpFetcher
import com.hobeen.collectorengine.port.Crawler
import com.hobeen.collectorengine.port.dto.CrawlingResult

abstract class AbstractPagedHtmlCrawler(
    private val httpFetcher: HttpFetcher,
    private val endPage: Int,
): Crawler {

    override fun crawling(url: String): CrawlingResult {

        val results = mutableListOf<String>()

        for(page in 1..endPage) {
            val pagedUrl = getPagedUrl(url, page)
            val body = httpFetcher.fetch(pagedUrl)
            results.add(body)
        }

        return CrawlingResult(
            htmls = results,
        )
    }

    abstract fun getPagedUrl(url: String, page: Int): String
}