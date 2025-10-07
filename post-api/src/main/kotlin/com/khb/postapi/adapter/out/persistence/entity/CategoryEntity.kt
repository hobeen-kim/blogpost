package com.khb.postapi.adapter.out.persistence.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(name = "category")
data class CategoryEntity (
    @Id
    val name: String,
)