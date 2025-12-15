package com.hobeen.adapterbanksalad

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.hobeen"])
@ConfigurationPropertiesScan("com.hobeen")
class AdapterBanksaladApplication

fun main(args: Array<String>) {
    runApplication<AdapterBanksaladApplication>(*args)
}
