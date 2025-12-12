package com.hobeen.collectoroutport.publisher.mock

import com.hobeen.collectorcommon.domain.Message
import com.hobeen.collectorengine.port.Publisher
import org.springframework.stereotype.Component

@Component("mockPublisher")
class MockPublisher: Publisher {

    override fun publish(messages: List<Message>) {
        messages.forEach {
            println(it)
        }
    }
}