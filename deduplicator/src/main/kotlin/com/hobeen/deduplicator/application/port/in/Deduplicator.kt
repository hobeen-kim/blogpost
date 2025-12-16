package com.hobeen.deduplicator.application.port.`in`

import com.hobeen.deduplicator.domain.Message

interface Deduplicator {

    fun saveIfNotDuplicated(message: Message)

    fun addDuplicateSet(urls: List<String>)

    fun clearDuplicateSet()
}