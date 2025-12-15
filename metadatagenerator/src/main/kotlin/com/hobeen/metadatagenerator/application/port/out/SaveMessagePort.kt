package com.hobeen.metadatagenerator.application.port.out

import com.hobeen.metadatagenerator.domain.EnrichedMessage

interface SaveMessagePort {

    fun save(message: EnrichedMessage)
}