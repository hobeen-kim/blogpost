package com.hobeen.batchtaglevel

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("com.hobeen")
class BatchTagLevelApplication

fun main(args: Array<String>) {
    runApplication<BatchTagLevelApplication>(*args)
}
