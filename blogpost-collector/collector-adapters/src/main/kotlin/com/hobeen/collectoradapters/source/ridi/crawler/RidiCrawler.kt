package com.hobeen.collectoradapters.source.ridi.crawler

import com.hobeen.collectoradapters.common.crawler.AbstractPagedHtmlCrawler
import com.hobeen.collectoradapters.common.fetcher.HttpFetcher
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