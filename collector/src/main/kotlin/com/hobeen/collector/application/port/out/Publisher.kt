package com.hobeen.collector.application.port.out

import com.hobeen.collector.domain.Message

interface Publisher {

    fun publish(messages: List<Message>)
}
