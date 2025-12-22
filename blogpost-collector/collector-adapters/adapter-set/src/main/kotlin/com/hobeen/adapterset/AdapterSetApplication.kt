package com.hobeen.adapterset

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(scanBasePackages = ["com.hobeen"])
@ConfigurationPropertiesScan("com.hobeen")
@EntityScan(basePackages = ["com.hobeen.collectoroutport"])           // 엔티티도 그쪽에 있으면 같이
@EnableJpaRepositories(basePackages = ["com.hobeen.collectoroutport"])
@EnableScheduling
class AdapterSetApplication

fun main(args: Array<String>) {
    runApplication<AdapterSetApplication>(*args)
}
