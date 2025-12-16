package com.hobeen.dlqprocessor.handler

import com.hobeen.dlqprocessor.domain.DlqMessage

interface CustomHandler {

    fun handle(message: DlqMessage, key: String)
}