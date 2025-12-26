package com.hobeen.blogpostcommon.alarm

import org.slf4j.LoggerFactory

class DefaultAlarmService: AlarmService {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun sendAlarm(alarmData: AlarmDto) {
        log.warn(alarmData.toString())
    }
}