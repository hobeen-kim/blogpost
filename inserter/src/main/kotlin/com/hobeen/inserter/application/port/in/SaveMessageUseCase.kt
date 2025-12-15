package com.hobeen.inserter.application.port.`in`

import com.hobeen.inserter.domain.EnrichedMessage

interface SaveMessageUseCase {

    fun save(message: EnrichedMessage)
}