package com.hobeen.inserter.application

import com.hobeen.inserter.application.port.`in`.SaveMessageUseCase
import com.hobeen.inserter.application.port.out.DlqPort
import com.hobeen.inserter.application.port.out.SaveMessagePort
import com.hobeen.inserter.domain.EnrichedMessage
import org.springframework.stereotype.Component

@Component
class Application(
    private val saveMessagePort: SaveMessagePort,
    private val dlqPort: DlqPort,
): SaveMessageUseCase {

    override fun save(message: EnrichedMessage) {
        saveMessagePort.save(message)
    }


}