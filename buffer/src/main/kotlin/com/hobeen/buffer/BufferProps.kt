package com.hobeen.buffer

data class CollectBufferProps(
    val sourceTopic: String = "deduplicated-post",
    val sinkTopic: String = "buffered-post",
    val groupId: String = "buffer-group",
    val pollMs: Long = 200,
    val idleSleepMs: Long = 50,
)
