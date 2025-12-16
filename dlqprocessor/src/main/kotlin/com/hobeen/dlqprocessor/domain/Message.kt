package com.hobeen.dlqprocessor.domain

import com.fasterxml.jackson.annotation.JsonIgnore

interface Message {

    @JsonIgnore
    fun topic(): String

    @JsonIgnore
    fun key(): String
}