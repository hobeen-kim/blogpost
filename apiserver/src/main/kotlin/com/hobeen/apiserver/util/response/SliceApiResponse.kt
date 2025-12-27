package com.hobeen.apiserver.util.response

import com.hobeen.apiserver.service.dto.SliceResponse
import org.springframework.data.domain.Page

data class SliceApiResponse<T> (
    val status: Int,
    val data: List<T>,
    val sliceInfo: SliceInfo,
) {
    companion object {
        fun <T> of(response: SliceResponse<T>): SliceApiResponse<T> {
            return SliceApiResponse(200, response.data, SliceInfo.of(response.size, response.hasNext))
        }
    }

}