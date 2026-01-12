package com.hobeen.collector.adapter.`in`.web.response

import com.hobeen.collector.domain.Message

data class TargetValidateResponse (
    val count: Int,
    val messages: List<Message>
) {
    companion object {
        fun of(messages: List<Message>): TargetValidateResponse {
            return TargetValidateResponse(
                count = messages.size,
                messages = messages
            )
        }
    }
}