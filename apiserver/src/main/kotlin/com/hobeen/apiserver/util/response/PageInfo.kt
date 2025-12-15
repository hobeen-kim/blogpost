package com.hobeen.apiserver.util.response

import org.springframework.data.domain.Page

data class PageInfo(
    val page: Int,
    val size: Int,
    val totalPages: Int,
    val totalElements: Long,
    val isLast: Boolean,
    val isFirst: Boolean,
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
                page.isFirst,
                page.isLast,
                page.hasNext(),
                page.hasPrevious()
            )
        }
    }
}