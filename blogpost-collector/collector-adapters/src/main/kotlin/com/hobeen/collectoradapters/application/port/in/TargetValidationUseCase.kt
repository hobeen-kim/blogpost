package com.hobeen.collectoradapters.application.port.`in`

import com.hobeen.collectoradapters.application.port.`in`.dto.TargetValidateCommand
import com.hobeen.collectoradapters.common.publisher.MemoryPublisher
import com.hobeen.collectorcommon.domain.CollectResult
import com.hobeen.collectorcommon.domain.Target
import java.time.LocalDateTime

interface TargetValidationUseCase {

    fun validate(command: TargetValidateCommand, publisher: MemoryPublisher)
}