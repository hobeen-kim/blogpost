package com.hobeen.collector.application

import com.hobeen.blogpostcommon.alarm.AlarmDto
import com.hobeen.blogpostcommon.alarm.AlarmService
import com.hobeen.collector.application.port.`in`.AlarmUseCase
import org.springframework.stereotype.Service

@Service
class CollectorAlarm(
    private val alarmService: AlarmService
): AlarmUseCase {
    override fun errorAlarm(message: String, exception: Exception) {
        alarmService.sendAlarm(AlarmDto(
            alarmMsg = message,
            source = "collector-adapters",
            url = "",
            rawData = "",
            exception = exception,
            exceptionPrintStackDepth = 2
        ))
    }
}