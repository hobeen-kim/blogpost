package com.hobeen.metadatagenerator.common

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId

private val seoulZone = ZoneId.of("Asia/Seoul")

fun localDateParse(dateStr: String): LocalDateTime {

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