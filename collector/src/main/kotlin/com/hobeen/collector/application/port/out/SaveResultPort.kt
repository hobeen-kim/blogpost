package com.hobeen.collector.application.port.out

import com.hobeen.collector.domain.CollectResult

interface SaveResultPort {

    fun save(results: List<CollectResult>)

    fun save(result: CollectResult)
}