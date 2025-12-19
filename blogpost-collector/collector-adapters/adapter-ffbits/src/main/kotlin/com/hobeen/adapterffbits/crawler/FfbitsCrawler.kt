package com.hobeen.adapterffbits.crawler

import com.hobeen.adaptercommon.crawler.AbstractPagedHtmlCrawler
import com.hobeen.adaptercommon.fetcher.HttpFetcher
import com.hobeen.adapterffbits.config.FfbitsProperties
import com.hobeen.collectorengine.port.Crawler
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.springframework.stereotype.Component

@Component
class FfbitsCrawler(
    fetcher: HttpFetcher,
    ffbitsProperties: FfbitsProperties
): AbstractPagedHtmlCrawler(
    httpFetcher = fetcher,
    endPage = ffbitsProperties.endPage,
) {
    override fun getPagedUrl(url: String, page: Int): String {
        return "$url?page=$page"
    }
}