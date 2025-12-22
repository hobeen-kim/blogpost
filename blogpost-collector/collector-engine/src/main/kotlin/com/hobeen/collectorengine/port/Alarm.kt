package com.hobeen.collectorengine.port

import com.hobeen.collectorengine.command.CollectCommand

interface Alarm {

    fun errorAlarm(command: CollectCommand, exception: Exception)

    fun errorAlarm(message: String, exception: Exception)
}