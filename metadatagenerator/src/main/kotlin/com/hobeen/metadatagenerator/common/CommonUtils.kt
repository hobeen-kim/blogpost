package com.hobeen.metadatagenerator.common

import java.text.ParsePosition
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val seoulZone = ZoneId.of("Asia/Seoul")


fun localDateParse(dateStr: String): LocalDateTime {

    //2024-05-29
    if(dateStr.length <= 10 && dateStr.split("-").size == 3) {
        val snippets = dateStr.split("-")

        val year = snippets[0].toInt()
        val month = snippets[1].toInt()
        val day = snippets[2].toInt()

        return LocalDateTime.of(year, month, day, 0, 0, 0)
    }

    //localdatetime
    if(dateStr.length <= 19) {
        return LocalDateTime.parse(dateStr)

    //zoned
    } else {
        val odt = OffsetDateTime.parse(dateStr)  // 2025-12-08T10:31:17Z (UTC)
        return odt.atZoneSameInstant(seoulZone).toLocalDateTime()
    }
}

fun refineTitle(title: String): String {
    return title.split("|").first().trim()
}