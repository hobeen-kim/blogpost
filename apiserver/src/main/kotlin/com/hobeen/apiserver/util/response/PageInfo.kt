package com.hobeen.apiserver.util.response

import org.springframework.data.domain.Page

data class PageInfo(
    val page: Int,
    val size: Int,
    val totalPages: Int,
    val totalElements: Long,
    val hasNext: Boolean,
    val hasPrevious: Boolean,
) {
    companion object {
        fun of(page: Page<*>): PageInfo {
            return PageInfo(
                page.number,
                page.size,
                page.totalPages,
                page.totalElements,
                page.hasNext(),
                page.hasPrevious()
            )
        }
    }
}