package com.hobeen.adapterset.source.ridi.crawler

import com.hobeen.adaptercommon.crawler.AbstractPagedHtmlCrawler
import com.hobeen.adaptercommon.fetcher.HttpFetcher
import org.springframework.stereotype.Component

@Component
class RidiCrawler(
    private val fetcher: HttpFetcher,
): AbstractPagedHtmlCrawler(
    httpFetcher = fetcher,
) {
    override fun getPagedUrl(url: String, page: Int): String {
        return if(page == 1) url
        else url + "page/$page/"
    }
}