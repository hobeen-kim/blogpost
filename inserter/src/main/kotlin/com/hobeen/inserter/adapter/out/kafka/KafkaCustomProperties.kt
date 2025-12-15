package com.hobeen.inserter.adapter.out.kafka

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "inserter.kafka")
data class KafkaCustomProperties (
    val producer: Producer,
) {
    data class Producer(
        val dlqTopic: String,
    )
}