package com.hobeen.collectoradapters

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(scanBasePackages = ["com.hobeen"])
@ConfigurationPropertiesScan("com.hobeen")
@EntityScan(basePackages = ["com.hobeen.collectoroutport"])
@EnableJpaRepositories(basePackages = ["com.hobeen.collectoroutport"])
@EnableScheduling
class CollectorAdaptersApplication

fun main(args: Array<String>) {
    runApplication<CollectorAdaptersApplication>(*args)
}