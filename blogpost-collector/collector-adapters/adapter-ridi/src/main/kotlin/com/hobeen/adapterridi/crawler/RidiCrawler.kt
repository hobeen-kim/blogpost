package com.hobeen.adapterridi.crawler

import com.hobeen.adaptercommon.crawler.AbstractPagedHtmlCrawler
import com.hobeen.adaptercommon.fetcher.HttpFetcher
import com.hobeen.adapterridi.config.RidiProperties
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.springframework.stereotype.Component

@Component
class RidiCrawler(
    private val fetcher: HttpFetcher,
    private val ridiProperties: RidiProperties,
): AbstractPagedHtmlCrawler(
    httpFetcher = fetcher,
    endPage = ridiProperties.endPage,
) {
    override fun getPagedUrl(url: String, page: Int): String {
        return if(page == 1) url
        else url + "page/$page/"
    }
}