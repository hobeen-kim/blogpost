package com.hobeen.dlqprocessor.domain

data class DlqMessage (
    val exception: String,
    val data: String,
    val message: String,
)

data class TypedDlqMessage<T> (
    val exception: String,
    val data: T,
    val message: String,
)