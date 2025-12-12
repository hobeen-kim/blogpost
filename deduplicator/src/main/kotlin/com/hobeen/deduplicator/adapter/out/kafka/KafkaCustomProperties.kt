package com.hobeen.deduplicator.adapter.out.kafka

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "deduplicator.kafka")
data class KafkaCustomProperties (
    val producer: Producer,
) {
    data class Producer(
        val topic: String,
        val dlqTopic: String,
    )
}