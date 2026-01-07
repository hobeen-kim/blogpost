package com.hobeen.apiserver.controller.dto

import jakarta.validation.constraints.NotBlank

data class BookmarkGroupUpdateApiRequest (
    @field:NotBlank
    val name: String,
)