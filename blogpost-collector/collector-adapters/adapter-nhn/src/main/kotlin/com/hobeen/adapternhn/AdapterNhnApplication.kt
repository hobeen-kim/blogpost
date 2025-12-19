package com.hobeen.adapternhn

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.hobeen"])
@ConfigurationPropertiesScan("com.hobeen")
class AdapterNhnApplication

fun main(args: Array<String>) {
    runApplication<AdapterNhnApplication>(*args)
}
