package com.hobeen.adapterkurly

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.hobeen"])
@ConfigurationPropertiesScan("com.hobeen")
class AdapterKurlyApplication

fun main(args: Array<String>) {
    runApplication<AdapterKurlyApplication>(*args)
}
