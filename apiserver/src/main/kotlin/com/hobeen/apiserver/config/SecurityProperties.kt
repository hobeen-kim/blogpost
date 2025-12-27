package com.hobeen.apiserver.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Profile

@ConfigurationProperties(prefix = "custom.security")
@Profile("aws")
class SecurityProperties (
    val cloudfrontSecret: String,
)