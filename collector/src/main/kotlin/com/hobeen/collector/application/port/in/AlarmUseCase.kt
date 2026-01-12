package com.hobeen.collector.application.port.`in`

interface AlarmUseCase {

    fun errorAlarm(message: String, exception: Exception)
}