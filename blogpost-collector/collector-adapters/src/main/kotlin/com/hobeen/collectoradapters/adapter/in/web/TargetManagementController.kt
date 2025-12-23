package com.hobeen.collectoradapters.adapter.`in`.web

import com.hobeen.collectoradapters.adapter.`in`.web.response.TargetValidateResponse
import com.hobeen.collectoradapters.application.port.`in`.TargetValidationUseCase
import com.hobeen.collectoradapters.application.port.`in`.dto.TargetValidateCommand
import com.hobeen.collectoradapters.common.publisher.MemoryPublisher
import com.hobeen.collectorcommon.domain.Message
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class TargetManagementController(
    private val targetValidationUseCase: TargetValidationUseCase
) {

    @PostMapping("targets/validate")
    fun validate(
        @RequestBody command: TargetValidateCommand
    ): ResponseEntity<TargetValidateResponse> {

        val publisher = MemoryPublisher()

        targetValidationUseCase.validate(command, publisher)



        return ResponseEntity.ok(TargetValidateResponse.of(
            messages = publisher.getMessages()
        ))
    }
}