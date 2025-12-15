package com.hobeen.metadatagenerator.application.port.`in`

import com.hobeen.metadatagenerator.domain.RawMessage

interface DlqUseCase {

    fun sendDlq(key: String, data: String, exception: Exception)

    fun sendDlq(key: String, data: RawMessage, exception: Exception)
}