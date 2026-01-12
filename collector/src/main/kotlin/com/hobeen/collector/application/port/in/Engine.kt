package com.hobeen.collector.application.port.`in`

import com.hobeen.collector.application.port.`in`.dto.CollectCommand
import com.hobeen.collector.domain.CollectResult

interface Engine {

    fun run(command: CollectCommand): CollectResult
}