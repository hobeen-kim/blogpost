package com.hobeen.adapterwatcha

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.hobeen"])
@ConfigurationPropertiesScan("com.hobeen")
class AdapterWatchaApplication

fun main(args: Array<String>) {
    runApplication<AdapterWatchaApplication>(*args)
}
