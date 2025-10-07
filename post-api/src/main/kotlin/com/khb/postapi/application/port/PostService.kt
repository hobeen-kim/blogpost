package com.khb.postapi.application.port

import com.khb.postapi.application.port.`in`.PostQuery
import com.khb.postapi.application.port.`in`.dto.GetPostCommand
import com.khb.postapi.application.port.`in`.dto.PagedResponse
import com.khb.postapi.application.port.out.GetPostPort
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class PostService(
    private val getPostPort: GetPostPort
): PostQuery {

    override fun getPosts(query: GetPostCommand): Mono<PagedResponse> {
        return getPostPort.getPosts(query)
            .collectList()
            .zipWith(getPostPort.count(query.category))
            .map { tuple ->
                PagedResponse(
                    content = tuple.t1,
                    totalCount = tuple.t2,
                    page = query.pageable.pageNumber,
                    size = query.pageable.pageSize,
                )

            }
    }
}