package com.hobeen.collector.adapter.`in`.scheduler

import com.hobeen.collector.application.port.`in`.CollectUseCase
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CollectSchedule (
    private val collectUseCase: CollectUseCase
) {

        @Scheduled(fixedRate = 1000 * 60L) // 1분마다
        fun collectAll() {
            collectUseCase.collectAllByCron(LocalDateTime.now())
        }

}