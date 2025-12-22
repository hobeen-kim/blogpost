package com.hobeen.collectoradapters.adapter.`in`.web

import com.hobeen.collectoradapters.application.port.`in`.CollectUseCase
import com.hobeen.collectorcommon.domain.CollectResult
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class CollectController (
    private val collectUseCase: CollectUseCase
) {

    @PostMapping("/sources/{sourceName}")
    fun collect(
        @PathVariable sourceName: String
    ): ResponseEntity<CollectResult> {

        val result = try {
             collectUseCase.collect(sourceName)
        } catch (e: Exception) {
            CollectResult.of(sourceName, e)
        }

        return ResponseEntity.ok(result)
    }


}