package com.hobeen.apiserver.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "custom.security")
class SecurityProperties (
    val cloudfrontSecret: String,
)