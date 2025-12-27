package com.hobeen.blogpostcommon.util

import org.springframework.cglib.core.Local
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

private val seoulZone = ZoneId.of("Asia/Seoul")
private val engFormatter = DateTimeFormatter.ofPattern("dd MMM, yyyy", Locale.ENGLISH)
private val korFormatter = DateTimeFormatter.ofPattern("M월 d, yyyy", Locale.KOREAN)
private val formatter6 = DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH)

fun localDateParse(dateStr: String?): LocalDateTime? {

    if(dateStr == null) return null

    //2024-05-29
    check1DateTime(dateStr)?.let { return it }

    //11 MAR, 2025
    check2DateTime(dateStr)?.let { return it }

    // 2025.12.24.
    check3DateTime(dateStr)?.let { return it }

    //2025년 03월 17일
    check4DateTime(dateStr)?.let { return it }

    //12월 9, 2025
    check5DateTime(dateStr)?.let { return it }

    //Tue, 16 Dec 2025 15:19:27
    check6DateTime(dateStr)?.let { return it }

    //localdatetime
    if(dateStr.length <= 19) {
        return LocalDateTime.parse(dateStr)

        //zoned
    } else {
        val odt = OffsetDateTime.parse(dateStr)  // 2025-12-08T10:31:17Z (UTC)
        return odt.atZoneSameInstant(seoulZone).toLocalDateTime()
    }
}

private fun check1DateTime(dateStr: String): LocalDateTime? {
    if(dateStr.length <= 10 && dateStr.split("-").size == 3) {
        val snippets = dateStr.split("-")

        val year = snippets[0].toInt()
        val month = snippets[1].toInt()
        val day = snippets[2].toInt()

        return LocalDateTime.of(year, month, day, 0, 0, 0)
    }

    return null
}

private fun check2DateTime(dateStr: String): LocalDateTime? {
    return try {
        LocalDate.parse(dateStr, engFormatter).atStartOfDay()
    } catch (e: DateTimeParseException) {
        null
    }
}

private fun check3DateTime(dateStr: String): LocalDateTime? {
    val dateSnippets = dateStr.trim().split(".")

    if(dateSnippets.size != 3 && dateSnippets.size != 4) return null

    val year = dateSnippets[0].toInt()
    val month = dateSnippets[1].toInt()
    val day = dateSnippets[2].toInt()

    return LocalDateTime.of(year, month, day, 0, 0, 0)
}

private fun check4DateTime(dateStr: String): LocalDateTime? {
    val dateSnippets = dateStr.trim().split(" ")

    if(dateSnippets.size != 3) return null

    if(!dateSnippets[0].contains("년")) return null

    val year = dateSnippets[0].substring(0, dateSnippets[0].length - 1).toInt()
    val month = dateSnippets[1].substring(0, dateSnippets[1].length - 1).toInt()
    val day = dateSnippets[2].substring(0, dateSnippets[2].length - 1).toInt()

    return LocalDateTime.of(year, month, day, 0, 0, 0)
}

private fun check5DateTime(dateStr: String): LocalDateTime? {
    return try {
        LocalDate.parse(dateStr, korFormatter).atStartOfDay()
    } catch (e: DateTimeParseException) {
        null
    }
}

private fun check6DateTime(dateStr: String): LocalDateTime? {
    return try {
        LocalDateTime.parse(dateStr, formatter6)
    } catch (e: DateTimeParseException) {
        null
    }
}