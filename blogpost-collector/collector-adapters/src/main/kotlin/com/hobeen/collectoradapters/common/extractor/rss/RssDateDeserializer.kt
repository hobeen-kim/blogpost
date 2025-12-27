package com.hobeen.collectoradapters.common.extractor.rss

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.hobeen.blogpostcommon.util.localDateParse
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

class RssDateDeserializer: JsonDeserializer<LocalDateTime>() {

    private val zone = ZoneId.of("Asia/Seoul")

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext?): LocalDateTime {
        val text = p.text.trim()


        return try { ZonedDateTime.parse(text, DateTimeFormatter.RFC_1123_DATE_TIME)
            .withZoneSameInstant(zone)
            .toLocalDateTime()
        } catch (e: DateTimeParseException) {
            localDateParse(dateStr = text) ?: throw IllegalArgumentException("cannot parse date at RssDateDeserializer : $text")
        }
    }
}