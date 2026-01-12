package com.hobeen.collector.adapter.out.crawler.common

import com.fasterxml.jackson.databind.JsonNode
import com.hobeen.collector.adapter.out.fetcher.HttpFetcher
import org.springframework.stereotype.Component

@Component
class PagedHtmlCrawler(
    fetcher: HttpFetcher,
): AbstractPagedHtmlCrawler(
    httpFetcher = fetcher,
) {
    override fun getPagedUrl(
        url: String,
        page: Int,
        props: JsonNode
    ): String {
        val prefix = props["page-prefix"]?.asText() ?: ""
        val suffix = props["page-suffix"]?.asText() ?: ""
        val firstPage = props["first-page"]?.asText()

        if(firstPage != null && page == 1) return firstPage

        return "$url$prefix$page$suffix"
    }
}