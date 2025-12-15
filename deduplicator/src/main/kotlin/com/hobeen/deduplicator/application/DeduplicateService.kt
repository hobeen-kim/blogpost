package com.hobeen.deduplicator.application

import com.hobeen.deduplicator.application.port.`in`.Deduplicator
import com.hobeen.deduplicator.application.port.out.DuplicateCheckPort
import com.hobeen.deduplicator.application.port.out.MessageSavePort
import com.hobeen.deduplicator.domain.Message
import org.springframework.stereotype.Component

@Component
class DeduplicateService(
    private val duplicateCheckPort: DuplicateCheckPort,
    private val messageSavePort: MessageSavePort,
): Deduplicator {

    override fun saveIfNotDuplicated(message: Message) {

        //중복 확인
        val isDup = duplicateCheckPort.checkAndSave(message.url)

        if(isDup) return

        //저장
        try {
            messageSavePort.save(message)
        } catch (e: Exception) {
            //저장 실패 시 중복을 제거
            duplicateCheckPort.delete(message.url)
            throw e
        }

    }

    override fun addDuplicateSet(urls: List<String>) {
        duplicateCheckPort.addDuplicateSet(urls)
    }

}