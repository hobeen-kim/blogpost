package com.hobeen.dlqprocessor.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.dlqprocessor.domain.DlqMessage
import com.hobeen.dlqprocessor.domain.TypedDlqMessage
import com.hobeen.dlqprocessor.handler.port.out.AlarmPort
import com.hobeen.dlqprocessor.handler.port.out.dto.AlarmDto

abstract class AbstractJsonHandler<T>(
    private val objectMapper: ObjectMapper,
    private val payloadType: Class<T>,
    private val alarmPort: AlarmPort,
): CustomHandler {

    override fun handle(message: DlqMessage, key: String) {
        val payload = try {
            objectMapper.readValue(message.data, payloadType)
        } catch (e: Exception) {
            handleException(message, key, e)
            return
        }

        try {
            handleTypeRecord(TypedDlqMessage(message.exception, payload, message.message))
        } catch (e: Exception) {
            handleTypedException(TypedDlqMessage(message.exception, payload, message.message), e)
        }
    }

    private fun handleException(message: DlqMessage, key: String, exception: Exception) {

        val dto = AlarmDto(
            message = "cannot read json data",
            source = "rawData",
            url = key,
            rawData = message.toString(),
            exception = exception,
        )

        alarmPort.sendAlarm(dto)
    }

    protected abstract fun handleTypeRecord(message: TypedDlqMessage<T>)

    protected abstract fun handleTypedException(message: TypedDlqMessage<T>, e: Exception)
}