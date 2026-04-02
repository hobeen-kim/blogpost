package com.hobeen.batchpostembedding

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("com.hobeen")
class BatchPostEmbeddingApplication

fun main(args: Array<String>) {
    runApplication<BatchPostEmbeddingApplication>(*args)
}
