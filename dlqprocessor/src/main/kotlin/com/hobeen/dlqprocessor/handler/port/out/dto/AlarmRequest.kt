package com.hobeen.dlqprocessor.handler.port.out.dto

data class AlarmRequest (
    val message: String,
    val source: String,
    val url: String,
    val rawData: String,
    val exception: Exception?
)