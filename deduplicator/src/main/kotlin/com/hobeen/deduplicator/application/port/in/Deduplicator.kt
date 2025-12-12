package com.hobeen.deduplicator.application.port.`in`

import com.hobeen.deduplicator.domain.Message

interface Deduplicator {

    fun saveIfNotDuplicated(message: Message)
}