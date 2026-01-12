package com.hobeen.collector.adapter.out.extractor.common.rss

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class RssV1DateDeserializer: JsonDeserializer<LocalDateTime>() {

    private val zone = ZoneId.of("Asia/Seoul")

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): LocalDateTime {
        val text = p.text.trim()
        return ZonedDateTime.parse(text, DateTimeFormatter.ISO_DATE_TIME)
            .withZoneSameInstant(zone)
            .toLocalDateTime()
    }
}