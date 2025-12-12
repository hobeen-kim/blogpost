package com.hobeen.deduplicator.application.port.`in`

interface DlqUseCase {

    fun sendDlq(key: String ,data: String, exception: Exception)
}