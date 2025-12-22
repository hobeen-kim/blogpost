package com.hobeen.collectorengine.port

import com.fasterxml.jackson.databind.JsonNode
import com.hobeen.collectorengine.port.dto.CrawlingResult

interface Crawler {

    fun crawling(url: String, props: JsonNode): CrawlingResult
}