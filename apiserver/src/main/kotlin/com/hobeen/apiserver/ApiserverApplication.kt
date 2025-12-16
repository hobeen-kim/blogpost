package com.hobeen.apiserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ApiserverApplication

fun main(args: Array<String>) {
    runApplication<ApiserverApplication>(*args)
}
