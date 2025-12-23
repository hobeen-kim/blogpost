package com.hobeen.collectorengine

import com.hobeen.collectorcommon.domain.CollectResult
import com.hobeen.collectorcommon.domain.CollectStatus
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
    private val alarm: Alarm? = null,
) {

    private val log = Logger.getLogger(this.javaClass.name)

    fun run(command: CollectCommand): CollectResult {

        try {
            //크롤링
            val crawlingResult = crawler.crawling(command.url, command.crawlerProps)

            //추출
            val messages = extractor.extract(crawlingResult, command.source, command.extractorProps)

            if(messages.isEmpty()) throw IllegalArgumentException("message is empty")

            //pub
            publisher.publish(messages)

            log.info("complete generate message from ${command.source} ${messages.size}")

            return CollectResult.of(source = command.source, messages.size)
        } catch(e: Exception) {
            alarm?.errorAlarm(command, e)
            return CollectResult.of(command.source, e)
        }
    }
}