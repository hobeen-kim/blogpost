package com.hobeen.collectoroutport.publisher.kafka

import com.hobeen.collectorcommon.domain.Message
import com.hobeen.collectorengine.port.Publisher
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaPublisher(
    private val kafkaTemplate: KafkaTemplate<String, Message>,
): Publisher {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun publish(messages: List<Message>) {
        messages.forEach { message ->
            try {
                kafkaTemplate.send("collector-post", message.source, message)
            } catch (e: Exception) {
                log.error("Failed to publish message to Kafka: ${message.title}", e)
            }
        }
    }
}