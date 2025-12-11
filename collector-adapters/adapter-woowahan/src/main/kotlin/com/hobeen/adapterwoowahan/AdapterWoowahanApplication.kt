package com.hobeen.adapterwoowahan

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["com.hobeen.adapterwoowahan", "com.hobeen.adaptercommon"])
class AdapterWoowahanApplication

fun main(args: Array<String>) {
    runApplication<AdapterWoowahanApplication>(*args)
}
