package com.hobeen.collector.application.port.`in`

import com.hobeen.collector.domain.CollectResult
import java.time.LocalDateTime

interface CollectUseCase {

    fun collectAllByCron(criteria: LocalDateTime)

    fun collectByName(targetName: String): CollectResult
}