package com.hobeen.collector.application

import com.hobeen.collector.application.port.`in`.dto.CollectCommand
import com.hobeen.collector.application.port.out.Alarm
import com.hobeen.collector.application.port.out.Crawler
import com.hobeen.collector.application.port.out.Extractor
import com.hobeen.collector.application.port.out.Publisher
import com.hobeen.collector.domain.CollectResult
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
            val crawlingResult = crawler.crawling(command.target.url, command.target.adapter.crawler)

            //추출
            val messages = extractor.extract(crawlingResult, command.target.source, command.target.adapter.extractor)

            if(messages.isEmpty()) throw IllegalArgumentException("message is empty")

            //pub
            publisher.publish(messages)

            log.info("complete generate message from ${command.target.source} ${messages.size}")

            return CollectResult.of(source = command.target.source, messages.size)
        } catch(e: Exception) {
            if(alarm != null) {
                alarm.errorAlarm(command, e)
            } else {
                e.printStackTrace()
            }
            return CollectResult.of(command.target.source, e)
        }
    }
}