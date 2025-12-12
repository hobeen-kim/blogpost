package com.hobeen.deduplicator.application.port.out

interface DlqPort {

    fun sendDlq(key: String, data: Map<String, String>)
}