package com.hobeen.adaptercommon.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.net.http.HttpClient

@Configuration
class CommonConfig {

    @Bean
    fun httpClient() = HttpClient.newBuilder().build()
}