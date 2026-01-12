package com.hobeen.collector.application.port.out

import com.hobeen.collector.application.port.`in`.dto.CollectCommand

interface Alarm {

    fun errorAlarm(command: CollectCommand, exception: Exception)

    fun errorAlarm(message: String, exception: Exception)
}