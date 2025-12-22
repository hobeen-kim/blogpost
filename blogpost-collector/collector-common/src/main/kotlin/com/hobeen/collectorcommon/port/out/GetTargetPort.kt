package com.hobeen.collectorcommon.port.out

import com.hobeen.collectorcommon.domain.Target
import java.time.LocalDateTime

interface GetTargetPort {

    fun getTargets(criteria: LocalDateTime): List<Target>

    fun getTarget(source: String): Target?
}