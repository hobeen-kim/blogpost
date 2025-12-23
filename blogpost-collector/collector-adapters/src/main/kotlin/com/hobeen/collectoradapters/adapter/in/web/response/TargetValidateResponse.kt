package com.hobeen.collectoradapters.adapter.`in`.web.response

import com.hobeen.collectorcommon.domain.Message

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