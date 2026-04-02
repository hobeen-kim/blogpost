package com.hobeen.dlqprocessor.handler.port.out.dto

data class AlarmRequest (
    val message: String,
    val source: String,
    val url: String,
    val rawData: String,
    val exception: Exception?
) {
    companion object {
        private const val MAX_RAW_DATA_LENGTH = 500
    }

    fun truncatedRawData(): String {
        return if (rawData.length > MAX_RAW_DATA_LENGTH) rawData.take(MAX_RAW_DATA_LENGTH) + "..." else rawData
    }
}