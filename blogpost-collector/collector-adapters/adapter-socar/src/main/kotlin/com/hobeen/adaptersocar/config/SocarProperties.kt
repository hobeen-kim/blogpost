package com.hobeen.adaptersocar.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "socar")
class SocarProperties {
    var endPage: Int = 20
}