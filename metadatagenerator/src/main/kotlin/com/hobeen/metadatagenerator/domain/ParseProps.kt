package com.hobeen.metadatagenerator.domain

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

data class ParseProps (
    val source: String,
    val parser: String,
    val props: JsonNode,
) {
    fun getProps(key: String, objectMapper: ObjectMapper): Map<String, String>? {
        val propNode = props[key] ?: return null

        return try {
            objectMapper.convertValue(propNode, object : TypeReference<Map<String, String>>() {})
        } catch (e: Exception) {
            null
        }
    }
}