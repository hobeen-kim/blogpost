package com.hobeen.collector.adapter.out.alarm

import com.hobeen.blogpostcommon.alarm.AlarmDto
import com.hobeen.blogpostcommon.alarm.AlarmService
import com.hobeen.collector.application.port.`in`.dto.CollectCommand
import com.hobeen.collector.application.port.out.Alarm
import org.springframework.stereotype.Component

@Component
class AlarmImpl(
    private val alarmService: AlarmService
): Alarm {
    override fun errorAlarm(command: CollectCommand, exception: Exception) {
        alarmService.sendAlarm(AlarmDto(
            alarmMsg = "${command.target.source} can not be collected",
            source = command.target.source,
            url = command.target.url,
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