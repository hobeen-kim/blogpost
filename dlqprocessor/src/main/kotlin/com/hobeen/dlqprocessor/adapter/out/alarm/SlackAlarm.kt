package com.hobeen.dlqprocessor.adapter.out.alarm

import com.hobeen.blogpostcommon.alarm.AlarmDto
import com.hobeen.blogpostcommon.alarm.AlarmService
import com.hobeen.dlqprocessor.handler.port.out.AlarmPort
import com.hobeen.dlqprocessor.handler.port.out.dto.AlarmRequest
import org.springframework.stereotype.Component

@Component
class SlackAlarm(
    private val alarmService: AlarmService,
): AlarmPort {

    override fun sendAlarm(alarmData: AlarmRequest) {

        alarmService.sendAlarm(
            AlarmDto(
                alarmMsg = alarmData.message,
                source = alarmData.source,
                url = alarmData.url,
                rawData = alarmData.rawData,
                exception = alarmData.exception,
                exceptionPrintStackDepth = 2
            )
        )
    }
}