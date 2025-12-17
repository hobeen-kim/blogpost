package com.hobeen.adapterridi.runner.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "ridi")
class RidiProperties {
    var endPage: Int = 3
}