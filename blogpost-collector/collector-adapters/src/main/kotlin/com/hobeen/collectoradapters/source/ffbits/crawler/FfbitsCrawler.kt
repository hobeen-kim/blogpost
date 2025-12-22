package com.hobeen.collectoradapters.source.ffbits.crawler

import com.hobeen.collectoradapters.common.crawler.AbstractPagedHtmlCrawler
import com.hobeen.collectoradapters.common.fetcher.HttpFetcher
import org.springframework.stereotype.Component

@Component
class FfbitsCrawler(
    fetcher: HttpFetcher,
): AbstractPagedHtmlCrawler(
    httpFetcher = fetcher,
) {
    override fun getPagedUrl(url: String, page: Int): String {
        return "$url?page=$page"
    }
}