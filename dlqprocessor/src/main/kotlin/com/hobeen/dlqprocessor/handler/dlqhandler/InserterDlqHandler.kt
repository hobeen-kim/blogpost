package com.hobeen.dlqprocessor.handler.dlqhandler

import com.fasterxml.jackson.databind.ObjectMapper
import com.hobeen.dlqprocessor.common.formatLastTwoCausesStacktrace
import com.hobeen.dlqprocessor.domain.EnrichedMessage
import com.hobeen.dlqprocessor.domain.TypedDlqMessage
import com.hobeen.dlqprocessor.handler.AbstractJsonHandler
import com.hobeen.dlqprocessor.handler.port.out.AlarmPort
import com.hobeen.dlqprocessor.handler.port.out.ReprocessPort
import org.springframework.stereotype.Component
import com.hobeen.dlqprocessor.handler.port.out.ProcessCounter
import com.hobeen.dlqprocessor.handler.port.out.dto.AlarmDto

@Component
class InserterDlqHandler(
    private val objectMapper: ObjectMapper,
    private val alarmPort: AlarmPort,
    private val reprocessPort: ReprocessPort,
    private val processCounter: ProcessCounter,
): AbstractJsonHandler<EnrichedMessage>(
    objectMapper = objectMapper,
    payloadType = EnrichedMessage::class.java,
    alarmPort = alarmPort,
) {
    override fun handleTypeRecord(message: TypedDlqMessage<EnrichedMessage>) {

        //post 중복 에러면 무시
        if(message.exception == "PostDuplicatedException") return

        //몇변처리된 건지 확인 (3번 이상이면 알림)
        if(processCounter.isOverReprocessLimit(message.data.url)) sendAlarm(alarmMessage = "reprocess limit over", message = message)

        //메타데이터가 다 있는지 확인 (없다면 알림) .. 딱히 할 수 있는게 없다 ;;
        if(message.data.hasAllValues().not()) sendAlarm(alarmMessage = "metadata is insufficient", message = message)

        //특수문자 제거 후 insert (\ / 제거)
        val enrichedMessage = EnrichedMessage(
            title = message.data.title,
            source = message.data.source,
            url = message.data.url,
            pubDate = message.data.pubDate,
            tags = message.data.tags,
            description = message.data.description.replace("\\", "").replace("/", ""),
            thumbnail = message.data.thumbnail,
        )

        //다시 insert 하도록 저장
        reprocessPort.save(enrichedMessage)
    }

    override fun handleTypedException(
        message: TypedDlqMessage<EnrichedMessage>,
        e: Exception
    ) {
        val dto = AlarmDto(
            message = formatLastTwoCausesStacktrace(e),
            source = message.data.source,
            url = message.data.url,
            rawData = message.toString(),
            exception = e,
        )

        alarmPort.sendAlarm(dto)
    }

    private fun sendAlarm(alarmMessage: String, message: TypedDlqMessage<EnrichedMessage>) {
        val dto = AlarmDto(
            message = alarmMessage,
            source = message.data.source,
            url = message.data.url,
            rawData = message.toString(),
            exception = null
        )

        alarmPort.sendAlarm(dto)
    }
}