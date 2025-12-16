package com.hobeen.dlqprocessor.domain

interface Message {

    fun topic(): String

    fun key(): String
}