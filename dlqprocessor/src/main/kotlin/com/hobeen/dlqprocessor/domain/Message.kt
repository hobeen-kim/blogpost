package com.hobeen.dlqprocessor.domain

interface Message {

    fun getTopic(): String

    fun getKey(): String
}