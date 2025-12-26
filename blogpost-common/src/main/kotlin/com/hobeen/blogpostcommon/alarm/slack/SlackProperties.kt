package com.hobeen.blogpostcommon.alarm.slack

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "alarm.slack")
data class SlackProperties (
    val url: String = "",
)