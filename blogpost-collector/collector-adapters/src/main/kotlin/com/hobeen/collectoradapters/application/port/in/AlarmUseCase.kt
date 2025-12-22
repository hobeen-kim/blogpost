package com.hobeen.collectoradapters.application.port.`in`

interface AlarmUseCase {

    fun errorAlarm(message: String, exception: Exception)
}