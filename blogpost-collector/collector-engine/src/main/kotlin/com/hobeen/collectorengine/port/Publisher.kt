package com.hobeen.collectorengine.port

import com.hobeen.collectorcommon.domain.Message

interface Publisher {

    fun publish(messages: List<Message>)
}
