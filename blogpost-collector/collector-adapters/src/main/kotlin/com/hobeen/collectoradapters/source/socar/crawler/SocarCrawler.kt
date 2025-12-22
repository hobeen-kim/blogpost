package com.hobeen.collectoradapters.source.socar.crawler

import com.hobeen.collectoradapters.common.crawler.AbstractPagedHtmlCrawler
import com.hobeen.collectoradapters.common.fetcher.HttpFetcher
import org.springframework.stereotype.Component

@Component
class SocarCrawler(
    fetcher: HttpFetcher,
): AbstractPagedHtmlCrawler(
    httpFetcher = fetcher,
) {
    override fun getPagedUrl(url: String, page: Int): String {

        if(page == 1) return url

        return "${url}page$page/"
    }
}