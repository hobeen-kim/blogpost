package com.hobeen.adapterwoowahan

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.hobeen"])
@ConfigurationPropertiesScan("com.hobeen")
class AdapterWoowahanApplication

fun main(args: Array<String>) {
    runApplication<AdapterWoowahanApplication>(*args)
}
