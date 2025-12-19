package com.hobeen.adaptersocar.crawler

import com.hobeen.adaptercommon.crawler.AbstractPagedHtmlCrawler
import com.hobeen.adaptercommon.fetcher.HttpFetcher
import com.hobeen.adaptersocar.config.SocarProperties
import org.springframework.stereotype.Component

@Component
class SocarCrawler(
    fetcher: HttpFetcher,
    socarProperties: SocarProperties
): AbstractPagedHtmlCrawler(
    httpFetcher = fetcher,
    endPage = socarProperties.endPage,
) {
    override fun getPagedUrl(url: String, page: Int): String {

        if(page == 1) return url

        return "${url}page$page/"
    }
}