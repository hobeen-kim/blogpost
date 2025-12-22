package com.hobeen.collectorcommon.domain

data class CollectResult (
    val source: String,
    val count: Int,
    val status: CollectStatus,
    val message: String
) {
    companion object {

        fun of(source: String, count: Int): CollectResult {
            return CollectResult(source = source, count = count, status = CollectStatus.SUCCESS, message = "success")
        }

        fun of(source: String, exception: Exception): CollectResult {
            return CollectResult(source = source, count = 0, status = CollectStatus.FAIL, message = "${exception.javaClass.simpleName} ${exception.message ?: "unknown error"}")
        }
    }
}

enum class CollectStatus {
    SUCCESS, FAIL
}