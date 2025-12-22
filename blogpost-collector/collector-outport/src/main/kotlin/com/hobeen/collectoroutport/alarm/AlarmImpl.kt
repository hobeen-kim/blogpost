package com.hobeen.collectoroutport.alarm

import com.hobeen.blogpostcommon.alarm.AlarmDto
import com.hobeen.blogpostcommon.alarm.AlarmService
import com.hobeen.collectorengine.command.CollectCommand
import com.hobeen.collectorengine.port.Alarm
import org.springframework.stereotype.Component

@Component
class AlarmImpl(
    private val alarmService: AlarmService
): Alarm {
    override fun errorAlarm(command: CollectCommand, exception: Exception) {
        alarmService.sendAlarm(AlarmDto(
            alarmMsg = "${command.source} can not be collected",
            source = command.source,
            url = command.url,
            rawData = "",
            exception = exception,
            exceptionPrintStackDepth = 2
        ))
    }

    override fun errorAlarm(message: String, exception: Exception) {
        alarmService.sendAlarm(AlarmDto(
            alarmMsg = message,
            source = "unknown",
            url = "unknown",
            rawData = "",
            exception = exception,
            exceptionPrintStackDepth = 2
        ))
    }
}