package com.hobeen.batchweeklyemail.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.ses.SesClient

@Configuration
class SesConfig {

    @Bean
    fun sesClient(@Value("\${aws.ses.region}") region: String): SesClient {
        return SesClient.builder()
            .region(Region.of(region))
            .build()
    }
}
