package com.hobeen.adaptertoss.extractor

import com.hobeen.adaptercommon.extractor.sitemap.SitemapExtractor
import com.hobeen.collectorcommon.domain.Message
import com.hobeen.collectorengine.port.dto.CrawlingResult
import org.springframework.stereotype.Component

@Component
class TossSitemapExtractor: SitemapExtractor() {

    override fun extract(crawlingResult: CrawlingResult, source: String): List<Message> {

        val filteredResults = crawlingResult.htmls.filter { it.contains("/article/") }

        return super.extract(CrawlingResult(filteredResults), source)
    }
}