package com.hobeen.adapterffbits

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.hobeen"])
@ConfigurationPropertiesScan("com.hobeen")
class AdapterFfbitsApplication

fun main(args: Array<String>) {
    runApplication<AdapterFfbitsApplication>(*args)
}
