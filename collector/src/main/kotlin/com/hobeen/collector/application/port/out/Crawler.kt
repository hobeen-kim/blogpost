package com.hobeen.collector.application.port.out

import com.hobeen.collector.application.port.`in`.dto.CrawlerProps
import com.hobeen.collector.application.port.out.dto.CrawlingResult

interface Crawler {

    fun crawling(url: String, props: CrawlerProps): CrawlingResult
}