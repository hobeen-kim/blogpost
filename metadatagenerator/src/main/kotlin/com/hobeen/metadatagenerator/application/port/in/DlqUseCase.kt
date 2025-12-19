package com.hobeen.metadatagenerator.application.port.`in`

interface DlqUseCase {

    fun sendDlq(key: String, data: String, exception: Exception)
}