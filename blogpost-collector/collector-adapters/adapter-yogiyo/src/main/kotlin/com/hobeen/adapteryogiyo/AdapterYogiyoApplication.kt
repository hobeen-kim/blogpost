package com.hobeen.adapteryogiyo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.hobeen"])
@ConfigurationPropertiesScan("com.hobeen")
class AdapterYogiyoApplication

fun main(args: Array<String>) {
    runApplication<AdapterYogiyoApplication>(*args)
}
