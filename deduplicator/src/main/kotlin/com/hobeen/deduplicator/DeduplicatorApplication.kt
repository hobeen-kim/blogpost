package com.hobeen.deduplicator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class DeduplicatorApplication

fun main(args: Array<String>) {
    runApplication<DeduplicatorApplication>(*args)
}
