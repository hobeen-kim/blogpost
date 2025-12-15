package com.hobeen.metadatagenerator.application.port.out

interface DlqPort {

    fun sendDlq(key: String, data: Map<String, String>)
}