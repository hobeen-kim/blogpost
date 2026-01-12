package com.hobeen.collector.domain

data class CollectResult (
    val source: String,
    val count: Int,
    val status: CollectStatus,
    val message: String,
    val exception: Exception? = null
) {
    companion object {

        fun of(source: String, count: Int): CollectResult {
            return CollectResult(source = source, count = count, status = CollectStatus.SUCCESS, message = "success")
        }

        fun of(source: String, exception: Exception): CollectResult {
            return CollectResult(source = source, count = 0, status = CollectStatus.FAIL, message = "${exception.javaClass.simpleName} ${exception.message ?: "unknown error"}", exception = exception)
        }
    }
}

enum class CollectStatus {
    SUCCESS, FAIL
}