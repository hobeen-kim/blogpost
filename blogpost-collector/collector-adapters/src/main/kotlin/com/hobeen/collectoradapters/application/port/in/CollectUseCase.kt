package com.hobeen.collectoradapters.application.port.`in`

import com.hobeen.collectorcommon.domain.CollectResult
import com.hobeen.collectorcommon.domain.Target
import java.time.LocalDateTime

interface CollectUseCase {

    fun collectAllByCron(criteria: LocalDateTime)

    fun collect(target: Target): CollectResult

    fun collect(targetName: String): CollectResult
}