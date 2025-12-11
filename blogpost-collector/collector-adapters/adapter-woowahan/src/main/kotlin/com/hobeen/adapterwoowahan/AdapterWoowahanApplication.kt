package com.hobeen.adapterwoowahan

import com.hobeen.adaptercommon.config.TargetProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.hobeen.*"])
@EnableConfigurationProperties(TargetProperties::class)
class AdapterWoowahanApplication

fun main(args: Array<String>) {
    runApplication<AdapterWoowahanApplication>(*args)
}
