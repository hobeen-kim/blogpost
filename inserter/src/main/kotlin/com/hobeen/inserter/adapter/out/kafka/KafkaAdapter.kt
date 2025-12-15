package com.hobeen.inserter.adapter.out.kafka

import com.hobeen.inserter.application.port.out.DlqPort
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaAdapter (
    private val dlqKafkaTemplate: KafkaTemplate<String, Map<String, String>>,
    private val kafkaCustomProperties: KafkaCustomProperties,
): DlqPort {

    override fun sendDlq(key: String, data: Map<String, String>) {
        dlqKafkaTemplate.send(kafkaCustomProperties.producer.dlqTopic, key, data)
    }
}