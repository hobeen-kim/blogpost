package com.hobeen.metadatagenerator.adapter.`in`.web

import com.hobeen.metadatagenerator.adapter.`in`.web.dto.ParserValidateRequest
import com.hobeen.metadatagenerator.application.port.`in`.ParserValidator
import com.hobeen.metadatagenerator.application.port.`in`.dto.HtmlResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/generator")
class ParserManagementController(
    private val parserValidator: ParserValidator
) {

    @PostMapping("/validate")
    fun validateParser(
        @RequestBody request: ParserValidateRequest,
    ): ResponseEntity<HtmlResponse> {
        return ResponseEntity.ok(parserValidator.validate(request.url, request.parser, request.props, request.metadata))
    }
}