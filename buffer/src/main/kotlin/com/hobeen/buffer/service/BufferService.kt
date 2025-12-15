package com.hobeen.buffer.service

import com.hobeen.buffer.CollectBufferProps
import org.apache.kafka.clients.consumer.Consumer
import org.springframework.context.SmartLifecycle
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

@Component
class BufferService(
    private val bufferExecutor: BufferExecutor,
): SmartLifecycle {

    private val running = AtomicBoolean(false)
    private val executor = Executors.newSingleThreadExecutor()

    override fun isRunning(): Boolean = running.get()


    override fun start() {
        if (!running.compareAndSet(false, true)) return
        executor.submit { bufferExecutor.run(running) }
    }

    override fun stop() {
        if (!running.compareAndSet(true, false)) return
        try {
            bufferExecutor.beforeStop()
        } catch (_: Exception) {}
        executor.shutdown()
        executor.awaitTermination(5, TimeUnit.SECONDS)
    }

    override fun getPhase(): Int = 0

    override fun isAutoStartup(): Boolean = true


}