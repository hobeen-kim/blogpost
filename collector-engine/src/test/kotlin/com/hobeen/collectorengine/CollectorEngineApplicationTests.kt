package com.hobeen.collectorengine

import com.hobeen.collectorengine.port.Crawler
import com.hobeen.collectorengine.port.dto.CrawlingResult
import kotlin.test.Test
import kotlin.test.assertNotNull

class CollectorEngineApplicationTests {

    @Test
    fun `engine is created with crawler dependency`() {
        val crawler = object : Crawler {
            override fun crawling(): CrawlingResult = CrawlingResult(emptyList())
        }

        val engine = Engine(crawler)

        assertNotNull(engine)
    }
}
