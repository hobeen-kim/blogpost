package com.hobeen.metadatagenerator.adapter.`in`.web

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ControllerAdvice {

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<String> {
        return ResponseEntity.badRequest().body(e.message)
    }

}