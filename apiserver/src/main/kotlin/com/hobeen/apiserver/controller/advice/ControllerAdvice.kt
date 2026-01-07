package com.hobeen.apiserver.controller.advice

import com.hobeen.apiserver.util.exception.BusinessException
import com.hobeen.apiserver.util.response.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ControllerAdvice {

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(e: BusinessException): ResponseEntity<ApiResponse<String>> {
        return ResponseEntity
            .status(e.code)
            .body(ApiResponse.Companion.of(e))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<String>> {
        e.printStackTrace()
        return ResponseEntity
            .status(500)
            .body(ApiResponse.Companion.of(e))
    }
}