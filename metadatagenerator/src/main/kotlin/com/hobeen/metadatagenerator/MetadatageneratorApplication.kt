package com.hobeen.metadatagenerator

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class MetadatageneratorApplication

fun main(args: Array<String>) {
    runApplication<MetadatageneratorApplication>(*args)
}
