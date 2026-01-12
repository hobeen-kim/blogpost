package com.hobeen.collector.adapter.`in`.web

import com.hobeen.collector.adapter.`in`.web.response.TargetValidateResponse
import com.hobeen.collector.adapter.out.publisher.mock.MemoryPublisher
import com.hobeen.collector.application.port.`in`.TargetValidationUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class TargetManagementController(
    private val targetValidationUseCase: TargetValidationUseCase
) {

    @PostMapping("targets/validate/{targetName}")
    fun validate(
        @PathVariable targetName: String
    ): ResponseEntity<TargetValidateResponse> {

        val publisher = MemoryPublisher()

        targetValidationUseCase.validate(targetName, publisher)

        return ResponseEntity.ok(
            TargetValidateResponse.of(
            messages = publisher.getMessages()
        ))
    }
}