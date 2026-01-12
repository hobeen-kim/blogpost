package com.hobeen.collector.adapter.out.publisher.mock

import com.hobeen.collector.application.port.out.Publisher
import com.hobeen.collector.domain.Message
import org.springframework.stereotype.Component

@Component("mockPublisher")
class MockPublisher: Publisher {

    override fun publish(messages: List<Message>) {
        messages.forEach {
            println(it)
        }
    }
}