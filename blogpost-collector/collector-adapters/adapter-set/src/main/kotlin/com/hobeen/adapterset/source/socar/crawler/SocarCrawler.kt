package com.hobeen.adapterset.source.socar.crawler

import com.hobeen.adaptercommon.crawler.AbstractPagedHtmlCrawler
import com.hobeen.adaptercommon.fetcher.HttpFetcher
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