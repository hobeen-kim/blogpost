package com.hobeen.apiserver.service.dto

data class CommentCreateRequest (
    val userId: String,
    val username: String,
    val comment: String,
)