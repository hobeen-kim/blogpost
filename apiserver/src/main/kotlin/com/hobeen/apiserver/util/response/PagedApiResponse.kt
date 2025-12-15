package com.hobeen.apiserver.util.response

import org.springframework.data.domain.Page

data class PagedApiResponse<T> (
    val status: Int,
    val data: List<T>,
    val pageInfo: PageInfo,
) {
    companion object {
        fun <T> of(pages: Page<T>): PagedApiResponse<T> {
            return PagedApiResponse(200, pages.content, PageInfo.of(pages))
        }
    }

}