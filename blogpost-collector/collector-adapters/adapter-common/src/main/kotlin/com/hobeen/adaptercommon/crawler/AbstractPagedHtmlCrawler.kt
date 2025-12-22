package com.hobeen.adaptercommon.crawler

import com.hobeen.adaptercommon.fetcher.HttpFetcher
import com.hobeen.collectorengine.port.Crawler
import com.hobeen.collectorengine.port.dto.CrawlingResult

abstract class AbstractPagedHtmlCrawler(
    private val httpFetcher: HttpFetcher,
): Crawler {

    override fun crawling(url: String, props: Map<String, String>): CrawlingResult {

        val results = mutableListOf<String>()

        val endPage = props["end-page"] ?: throw IllegalArgumentException("crawling end-page is not set")
        if(endPage.toIntOrNull() == null || endPage.toInt() < 1) { throw IllegalArgumentException("crawling endPage is not valid") }

        for(page in 1..endPage.toInt()) {
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