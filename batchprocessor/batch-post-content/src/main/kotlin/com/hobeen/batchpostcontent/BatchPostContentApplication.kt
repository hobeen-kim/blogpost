package com.hobeen.batchpostcontent

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("com.hobeen")
class BatchPostContentApplication

fun main(args: Array<String>) {
    runApplication<BatchPostContentApplication>(*args)
}
