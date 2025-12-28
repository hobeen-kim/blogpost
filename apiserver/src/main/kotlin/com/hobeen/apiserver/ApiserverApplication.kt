package com.hobeen.apiserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableJpaAuditing
@EnableScheduling
class ApiserverApplication

fun main(args: Array<String>) {
    runApplication<ApiserverApplication>(*args)
}
