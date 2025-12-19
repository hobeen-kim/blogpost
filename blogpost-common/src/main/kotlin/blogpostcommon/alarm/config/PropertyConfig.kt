package com.hobeen.blogpostcommon.alarm.config

import com.hobeen.blogpostcommon.alarm.slack.SlackProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(SlackProperties::class)
class PropertyConfig {
}