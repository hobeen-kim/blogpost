package com.hobeen.adapternhn.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "nhn")
class NhnProperties {
    var perPage: Int = 20
}