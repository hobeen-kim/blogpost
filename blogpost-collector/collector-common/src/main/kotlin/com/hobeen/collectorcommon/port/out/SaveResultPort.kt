package com.hobeen.collectorcommon.port.out

import com.hobeen.collectorcommon.domain.CollectResult

interface SaveResultPort {

    fun save(results: List<CollectResult>)

    fun save(result: CollectResult)
}