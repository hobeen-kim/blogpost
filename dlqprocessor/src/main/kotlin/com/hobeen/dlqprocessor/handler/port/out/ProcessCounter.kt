package com.hobeen.dlqprocessor.handler.port.out

import com.hobeen.dlqprocessor.domain.Message

interface ProcessCounter {

    fun isOverReprocessLimit(url: String): Boolean
}