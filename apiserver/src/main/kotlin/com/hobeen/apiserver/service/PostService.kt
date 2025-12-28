package com.hobeen.apiserver.service

import com.hobeen.apiserver.repository.BookmarkRepository
import com.hobeen.apiserver.repository.CommentRepository
import com.hobeen.apiserver.repository.LikeRepository
import com.hobeen.apiserver.repository.PostRepository
import com.hobeen.apiserver.service.dto.PostResponse
import com.hobeen.apiserver.service.dto.SourceResponse
import com.hobeen.apiserver.util.auth.authUserId
import com.hobeen.apiserver.util.auth.isLogin
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postRepository: PostRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val likeRepository: LikeRepository,
    private val commentRepository: CommentRepository,

    private val metadataService: MetadataService,
) {

    private val sources = mutableMapOf<String, SourceResponse>()

    fun getPosts(search: String?, sources: List<String>?, pageable: Pageable): Page<PostResponse> {

        val posts = postRepository.findBySearch(search = search, sources = sources , pageable = pageable)

        val postIds = posts.content.map { it.postId }

        if(isLogin()) {
            val bookmarked = bookmarkRepository.existsByPostIds(authUserId(), postIds)
            val liked = likeRepository.existsByPostIds(authUserId(), postIds)
            val commented = commentRepository.existsByPostIds(authUserId(), postIds)

            return posts.map {
                PostResponse.of(
                    post = it,
                    metadata = metadataService.getMetadata(it.source),
                    bookmarked = bookmarked[it.postId] == true,
                    liked = liked[it.postId] == true,
                    commented = commented[it.postId] == true,
                ) }
        } else {
            return posts.map { PostResponse.of(
                post = it,
                metadata = metadataService.getMetadata(it.source),
                bookmarked = false,
                liked = false,
                commented = false,
            ) }
        }
    }

    fun getSources(): List<SourceResponse> {

        return sources.values.toList()
    }

    @Scheduled(fixedRate = 1000L * 60 * 60 * 1) //1시간
    fun refreshSources() {
        val sources = postRepository.findAllSources().associate {
            it.source to
            SourceResponse(
                source = it.source,
                count = it.count.toInt(),
                metadataCache = metadataService.getMetadata(it.source)
            )
        }

        //upsert
        sources.forEach {
            this.sources[it.key] = it.value
        }

        //delete
        this.sources.forEach {
            if(sources[it.key] == null) {
                this.sources.remove(it.key)
            }
        }
    }
}