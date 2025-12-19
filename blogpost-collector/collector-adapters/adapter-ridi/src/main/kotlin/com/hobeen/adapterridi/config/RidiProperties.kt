package com.hobeen.adapterridi.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "ridi")
class RidiProperties {
    var endPage: Int = 3
}