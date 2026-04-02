package com.hobeen.batchweeklyemail.dto

data class EmailRecommendation(
    val userEmail: String,
    val userId: String,
    val posts: List<RecommendedPost>,
)
