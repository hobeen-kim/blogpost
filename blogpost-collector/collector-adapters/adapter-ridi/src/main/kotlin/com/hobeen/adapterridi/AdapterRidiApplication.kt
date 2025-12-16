package com.hobeen.adapterridi

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.hobeen"])
@ConfigurationPropertiesScan("com.hobeen")
class AdapterRidiApplication

fun main(args: Array<String>) {
    runApplication<AdapterRidiApplication>(*args)
}
