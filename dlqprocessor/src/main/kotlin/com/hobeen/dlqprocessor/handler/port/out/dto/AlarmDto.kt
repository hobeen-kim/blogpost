package com.hobeen.dlqprocessor.handler.port.out.dto

data class AlarmDto (
    val message: String,
    val source: String,
    val url: String,
    val rawData: String,
    val exception: Exception?
) {
    fun toText(): String {
        return """
--------------ALARM----------------
**message**=$message
**source**=$source
**url**=$url
**rawData**=$rawData
**exception**=${exception?.let { exception::class.java.simpleName }}
        """.trimIndent()
    }
}