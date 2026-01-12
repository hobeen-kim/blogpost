package com.hobeen.collector.application.port.out

import java.time.LocalDateTime
import com.hobeen.collector.application.port.`in`.dto.Target

interface GetTargetPort {

    fun getTargets(criteria: LocalDateTime): List<Target>

    fun getTarget(targetName: String): Target?
}