package com.hobeen.metadatagenerator.application

import com.hobeen.metadatagenerator.application.port.`in`.DlqUseCase
import com.hobeen.metadatagenerator.application.port.out.DlqPort
import com.hobeen.metadatagenerator.domain.RawMessage
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class DlqService(
    private val dlqPort: DlqPort
): DlqUseCase {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun sendDlq(key: String, data: String, exception: Exception) {

        val dlqData: MutableMap<String, String> = HashMap()
        dlqData["data"] = data
        dlqData["exception"] = exception.javaClass.getSimpleName()
        dlqData["message"] = exception.message ?: "unknown error message"

        dlqPort.sendDlq(key, dlqData)

        log.warn("sent dlq : ${key}")
    }

    override fun sendDlq(
        key: String,
        data: RawMessage,
        exception: Exception
    ) {
        val dlqData: MutableMap<String, String> = HashMap()
        dlqData["data"] = data.toString()
        dlqData["exception"] = exception.javaClass.getSimpleName()
        dlqData["message"] = exception.message ?: "unknown error message"

        dlqPort.sendDlq(key, dlqData)

        log.warn("sent dlq : ${key}")
    }
}