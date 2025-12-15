package com.hobeen.metadatagenerator.application.port.`in`

import com.hobeen.metadatagenerator.domain.EnrichedMessage
import com.hobeen.metadatagenerator.domain.RawMessage

interface MetadataGenerator {

    fun generate(message: RawMessage): EnrichedMessage

    fun save(message: EnrichedMessage)

}