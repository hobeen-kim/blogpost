package com.hobeen.apiserver.service.dto

data class CommentUpdateRequest (
    val userId: String,
    val username: String,
    val comment: String,
)