package com.hobeen.inserter.application.port.out

import com.hobeen.inserter.domain.EnrichedMessage

interface SaveMessagePort {

    fun save(message: EnrichedMessage)
}