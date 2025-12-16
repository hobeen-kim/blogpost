package com.hobeen.inserter.adapter.`in`.web

import com.hobeen.blogpostcommon.exception.BusinessException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ControllerAdvice {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(exception: BusinessException): ResponseEntity<String> {

        return ResponseEntity
            .status(exception.status)
            .body(exception.message)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(exception: Exception): ResponseEntity<String> {

        return ResponseEntity
            .status(500)
            .body(exception.message)
    }
}