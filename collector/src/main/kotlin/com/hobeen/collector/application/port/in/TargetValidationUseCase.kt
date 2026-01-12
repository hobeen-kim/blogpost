package com.hobeen.collector.application.port.`in`

import com.hobeen.collector.adapter.out.publisher.mock.MemoryPublisher

interface TargetValidationUseCase {

    fun validate(targetName: String, publisher: MemoryPublisher)
}