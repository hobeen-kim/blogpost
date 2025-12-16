package com.hobeen.dlqprocessor.handler.port.out

import com.hobeen.dlqprocessor.domain.Message

interface ReprocessPort {

    fun save(message: Message)
}