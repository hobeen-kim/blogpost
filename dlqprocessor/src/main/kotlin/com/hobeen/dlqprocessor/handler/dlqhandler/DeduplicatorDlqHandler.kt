package com.hobeen.dlqprocessor.handler.dlqhandler

import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.dlqprocessor.domain.RawMessage
import com.hobeen.dlqprocessor.domain.TypedDlqMessage
import com.hobeen.dlqprocessor.handler.AbstractJsonHandler
import com.hobeen.dlqprocessor.handler.port.out.AlarmPort
import com.hobeen.dlqprocessor.handler.port.out.dto.AlarmRequest
import org.springframework.stereotype.Component

@Component
class DeduplicatorDlqHandler(
    private val objectMapper: ObjectMapper,
    private val alarmPort: AlarmPort,
): AbstractJsonHandler<RawMessage>(
    objectMapper = objectMapper,
    payloadType = RawMessage::class.java,
    alarmPort = alarmPort,
) {
    override fun handleTypeRecord(message: TypedDlqMessage<RawMessage>) {
        val dto = AlarmRequest(
            message = "Dlqprocessor:DeduplicatedDlqHandler - dql generated alarm",
            source = message.data.source,
            url = message.data.url,
            rawData = message.toString(),
            exception = null,
        )

        alarmPort.sendAlarm(dto)
    }

    override fun handleTypedException(
        message: TypedDlqMessage<RawMessage>,
        e: Exception
    ) {
        val dto = AlarmRequest(
            message = "Dlqprocessor:DeduplicatedDlqHandler",
            source = message.data.source,
            url = message.data.url,
            rawData = message.toString(),
            exception = null,
        )

        alarmPort.sendAlarm(dto)
    }
}