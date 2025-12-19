package com.hobeen.adapternhn.crawler

import com.hobeen.adaptercommon.fetcher.HttpFetcher
import com.hobeen.adapternhn.config.NhnProperties
import com.hobeen.collectorengine.port.Crawler
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.springframework.stereotype.Component

@Component
class NhnCrawler(
    private val fetcher: HttpFetcher,
    private val nhnProperties: NhnProperties
): Crawler {
    override fun crawling(url: String): CrawlingResult {
        val result = fetcher.fetch("$url?rowsPerPage=${nhnProperties.perPage}")

        return CrawlingResult(
            htmls = listOf(result),
        )
    }
}