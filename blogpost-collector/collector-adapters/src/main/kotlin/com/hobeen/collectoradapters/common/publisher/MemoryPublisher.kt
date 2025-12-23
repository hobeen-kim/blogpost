package com.hobeen.collectoradapters.common.publisher

import com.hobeen.collectorcommon.domain.Message
import com.hobeen.collectorengine.port.Publisher

class MemoryPublisher: Publisher {

    private val memory = mutableListOf<Message>()

    override fun publish(messages: List<Message>) {
        memory.addAll(messages)
    }

    fun getMessages(): List<Message> {
        return memory
    }
}