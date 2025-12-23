package com.hobeen.blogpostcommon.alarm.config

import com.hobeen.blogpostcommon.alarm.AlarmService
import com.hobeen.blogpostcommon.alarm.DefaultAlarmService
import com.hobeen.blogpostcommon.alarm.slack.SlackAlarmService
import com.hobeen.blogpostcommon.alarm.slack.SlackProperties
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean

@AutoConfiguration
@EnableConfigurationProperties(SlackProperties::class)
class AlarmAutoConfiguration {
    @Bean
    @ConditionalOnProperty(prefix = "alarm.slack", name = ["enabled"], havingValue = "true")
    fun slackAlarmService(props: SlackProperties): AlarmService {
        return SlackAlarmService(props)
    }

    @Bean
    @ConditionalOnMissingBean(AlarmService::class)
    fun noopAlarmService(): AlarmService = DefaultAlarmService()
}