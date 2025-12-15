package com.hobeen.buffer.service

import java.util.concurrent.atomic.AtomicBoolean

interface BufferExecutor {

    fun run(runningMark: AtomicBoolean)

    fun beforeStop()
}