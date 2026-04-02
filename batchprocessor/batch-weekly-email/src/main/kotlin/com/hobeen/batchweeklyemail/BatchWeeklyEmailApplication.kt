package com.hobeen.batchweeklyemail

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan("com.hobeen")
class BatchWeeklyEmailApplication

fun main(args: Array<String>) {
    runApplication<BatchWeeklyEmailApplication>(*args)
}
