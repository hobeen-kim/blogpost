package com.hobeen.dlqprocessor.handler.port.out

import com.hobeen.dlqprocessor.handler.port.out.dto.AlarmRequest

interface AlarmPort {

    fun sendAlarm(alarmData: AlarmRequest)
}