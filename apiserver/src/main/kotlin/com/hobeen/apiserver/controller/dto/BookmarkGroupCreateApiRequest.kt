package com.hobeen.apiserver.controller.dto

import jakarta.validation.constraints.NotBlank

data class BookmarkGroupCreateApiRequest (
    @field:NotBlank
    val name: String,
)