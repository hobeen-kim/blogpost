package com.hobeen.collectorengine

import com.hobeen.collectorengine.command.CollectCommand
import com.hobeen.collectorengine.port.Crawler
import com.hobeen.collectorengine.port.Extractor
import com.hobeen.collectorengine.port.Publisher

class Engine(
    private val crawler: Crawler,
    private val extractor: Extractor,
    private val publisher: Publisher,
) {

    fun run(command: CollectCommand) {

        //크롤링
        val crawlingResult = crawler.crawling(command.url)

        //추출
        val messages = extractor.extract(crawlingResult)

        //pub
        publisher.publish(messages)
    }
}