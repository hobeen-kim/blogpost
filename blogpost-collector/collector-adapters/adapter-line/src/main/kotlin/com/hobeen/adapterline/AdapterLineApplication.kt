package com.hobeen.adapterline

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.hobeen"])
@ConfigurationPropertiesScan("com.hobeen")
class AdapterLineApplication

fun main(args: Array<String>) {
    runApplication<AdapterLineApplication>(*args)
}
