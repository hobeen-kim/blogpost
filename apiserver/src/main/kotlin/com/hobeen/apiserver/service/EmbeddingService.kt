package com.hobeen.apiserver.service

import org.springframework.ai.embedding.EmbeddingModel
import org.springframework.stereotype.Service

@Service
class EmbeddingService(
    private val embeddingModel: EmbeddingModel,
) {

    fun embed(text: String): List<Double> {
        return embeddingModel.embed(text).map { it.toDouble() }
    }
}
