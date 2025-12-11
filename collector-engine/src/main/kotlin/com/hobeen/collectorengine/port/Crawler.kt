package com.hobeen.collectorengine.port

import com.hobeen.collectorengine.port.dto.CrawlingResult

interface Crawler {

    fun crawling(url: String): CrawlingResult
}