package com.hobeen.apiserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class ApiserverApplication

fun main(args: Array<String>) {
    runApplication<ApiserverApplication>(*args)
}
