package com.hobeen.inserter.application

import com.hobeen.inserter.application.port.`in`.DlqUseCase
import com.hobeen.inserter.application.port.out.DlqPort
import org.springframework.stereotype.Component

@Component
class DlqService(
    private val dlqPort: DlqPort
): DlqUseCase {

    override fun sendDlq(key: String, data: String, exception: Exception) {

        val dlqData: MutableMap<String, String> = HashMap()
        dlqData["data"] = data
        dlqData["exception"] = exception.javaClass.getSimpleName()
        dlqData["message"] = exception.message ?: "unknown error message"

        dlqPort.sendDlq(key, dlqData)
    }
}