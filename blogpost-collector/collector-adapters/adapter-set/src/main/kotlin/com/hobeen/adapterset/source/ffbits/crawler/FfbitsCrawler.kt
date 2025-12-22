package com.hobeen.adapterset.source.ffbits.crawler

import com.hobeen.adaptercommon.crawler.AbstractPagedHtmlCrawler
import com.hobeen.adaptercommon.fetcher.HttpFetcher
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