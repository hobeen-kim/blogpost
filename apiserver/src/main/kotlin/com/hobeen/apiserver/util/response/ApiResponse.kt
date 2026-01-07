package com.hobeen.apiserver.util.response

import com.hobeen.apiserver.util.exception.BusinessException

data class ApiResponse<T> (
    val status: Int,
    val data: T,
) {
    companion object {
        fun <T> of(data: T): ApiResponse<T> {
            return ApiResponse(200, data)
        }

        fun of(exception: BusinessException): ApiResponse<String> {
            return ApiResponse(exception.code, exception.message)
        }

        fun of(exception: Exception): ApiResponse<String> {
            return ApiResponse(500, "unknown error")
        }
    }

}