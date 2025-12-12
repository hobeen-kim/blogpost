package com.hobeen.deduplicator.application.port.out

import com.hobeen.deduplicator.domain.Message

interface MessageSavePort {

    fun save(message: Message)
}