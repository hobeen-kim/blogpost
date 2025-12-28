package com.hobeen.inserter.adapter.`in`.web

import com.hobeen.inserter.adapter.`in`.web.dto.PostRequest
import com.hobeen.inserter.application.port.`in`.SaveMessageUseCase
import com.hobeen.inserter.domain.EnrichedMessage
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/posts")
class PostController(
    private val savePostUseCase: SaveMessageUseCase
) {

    @PostMapping
    fun create(
        @RequestBody request: PostRequest,
    ): ResponseEntity<String> {
        savePostUseCase.save(EnrichedMessage(
            title = request.title,
            source = request.source,
            url = request.url,
            pubDate = request.pubDate,
            tags = request.tags,
            description = request.description,
            thumbnail = request.thumbnail,
            content = "",
        ))

        return ResponseEntity.ok("Post created successfully")
    }

    @PutMapping
    fun update(
        @RequestBody request: PostRequest,
    ): ResponseEntity<String> {
        savePostUseCase.update(EnrichedMessage(
            title = request.title,
            source = request.source,
            url = request.url,
            pubDate = request.pubDate,
            tags = request.tags,
            description = request.description,
            thumbnail = request.thumbnail,
            content = "",
        ))

        return ResponseEntity.ok("Post updated successfully")
    }
}