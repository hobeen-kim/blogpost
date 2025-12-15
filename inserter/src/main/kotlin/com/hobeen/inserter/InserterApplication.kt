package com.hobeen.inserter

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan

class InserterApplication

fun main(args: Array<String>) {
    runApplication<InserterApplication>(*args)
}
