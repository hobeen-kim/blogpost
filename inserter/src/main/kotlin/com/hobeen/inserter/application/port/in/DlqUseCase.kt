package com.hobeen.inserter.application.port.`in`

interface DlqUseCase {

    fun sendDlq(key: String ,data: String, exception: Exception)
}