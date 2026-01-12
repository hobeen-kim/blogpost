package com.hobeen.collector.application.port.`in`

import com.hobeen.collector.application.port.`in`.dto.Target
import com.hobeen.collector.domain.CollectResult
import java.time.LocalDateTime

interface CollectUseCase {

    fun collectAllByCron(criteria: LocalDateTime)

    fun collect(target: Target): CollectResult

    fun collect(targetName: String): CollectResult
}