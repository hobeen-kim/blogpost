package com.hobeen.collectorengine

import com.hobeen.collectorengine.command.CollectCommand
import com.hobeen.collectorengine.port.Alarm
import com.hobeen.collectorengine.port.Crawler
import com.hobeen.collectorengine.port.Extractor
import com.hobeen.collectorengine.port.Publisher
import java.util.logging.Logger

class Engine(
    private val crawler: Crawler,
    private val extractor: Extractor,
    private val publisher: Publisher,
    private val alarm: Alarm,
) {

    private val log = Logger.getLogger(this.javaClass.name)

    fun run(command: CollectCommand) {

        try {
            //크롤링
            val crawlingResult = crawler.crawling(command.url)

            //추출
            val messages = extractor.extract(crawlingResult, command.source)

            //pub
            publisher.publish(messages)

            log.info("complete generate message ${messages.size}")
        } catch(e: Exception) {
            alarm.errorAlarm(command, e)
        }
    }
}