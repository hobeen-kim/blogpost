package com.hobeen.collector.adapter.out.publisher.mock

import com.hobeen.collector.application.port.out.Publisher
import com.hobeen.collector.domain.Message

class MemoryPublisher: Publisher {

    private val memory = mutableListOf<Message>()

    override fun publish(messages: List<Message>) {
        memory.addAll(messages)
    }

    fun getMessages(): List<Message> {
        return memory
    }
}