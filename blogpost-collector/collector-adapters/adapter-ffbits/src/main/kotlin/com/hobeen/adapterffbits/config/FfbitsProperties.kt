package com.hobeen.adapterffbits.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "ffbits")
class FfbitsProperties {
    var endPage: Int = 20
}