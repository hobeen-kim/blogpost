package com.hobeen.dlqprocessor.handler.port.out

import com.hobeen.dlqprocessor.handler.port.out.dto.AlarmDto

interface AlarmPort {

    fun sendAlarm(alarmData: AlarmDto)
}