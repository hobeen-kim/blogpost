package com.hobeen.apiserver.controller

import com.hobeen.apiserver.service.AskService
import com.hobeen.apiserver.service.dto.AskRequest
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
class AskController(
    private val askService: AskService,
) {

    @PostMapping("/ask", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun ask(
        @RequestBody request: AskRequest,
        @AuthenticationPrincipal jwt: Jwt,
    ): SseEmitter {
        val emitter = SseEmitter(60_000L)
        Thread {
            try {
                askService.ask(request, emitter)
            } catch (e: Exception) {
                emitter.send(SseEmitter.event().data("""{"type":"error","content":"서버 오류가 발생했습니다"}"""))
                emitter.complete()
            }
        }.start()
        return emitter
    }
}
