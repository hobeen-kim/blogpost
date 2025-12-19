package com.hobeen.apiserver.util.response

data class ApiResponse<T> (
    val status: Int,
    val data: T,
) {
    companion object {
        fun <T> of(data: T): ApiResponse<T> {
            return ApiResponse(200, data)
        }
    }

}