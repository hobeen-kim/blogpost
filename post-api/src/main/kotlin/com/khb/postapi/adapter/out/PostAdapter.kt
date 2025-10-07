package com.khb.postapi.adapter.out

import com.khb.postapi.adapter.out.persistence.repository.PostRepository
import com.khb.postapi.application.port.`in`.dto.GetPostCommand
import com.khb.postapi.application.port.out.GetPostPort
import com.khb.postapi.domain.Post
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Component
class PostAdapter(
    private val postRepository: PostRepository
): GetPostPort {

    override fun getPosts(command: GetPostCommand): Flux<Post> {

        val postEntities = postRepository.findAll(
            command.pageable,
            command.category
        )

        return postEntities.map { it.toDomain() }

    }

    override fun count(category: String?): Mono<Long> {
        return postRepository.count(category)
    }
}