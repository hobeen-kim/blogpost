package com.hobeen.apiserver.service

import com.hobeen.apiserver.repository.BookmarkRepository
import com.hobeen.apiserver.repository.CommentRepository
import com.hobeen.apiserver.repository.LikeRepository
import com.hobeen.apiserver.repository.PostRepository
import com.hobeen.apiserver.repository.PostVectorRepository
import com.hobeen.apiserver.service.dto.PostResponse
import com.hobeen.apiserver.service.dto.SourceResponse
import com.hobeen.apiserver.util.auth.authUserId
import com.hobeen.apiserver.util.auth.isLogin
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
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
    private val postVectorRepository: PostVectorRepository,
    private val embeddingService: EmbeddingService,
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

    fun getPostsSemantic(search: String, sources: List<String>?, pageable: Pageable): Page<PostResponse> {
        // 1. Embed search query
        val embedding = embeddingService.embed(search)
        val embeddingStr = embedding.joinToString(", ", "[", "]")

        // 2. Vector search with paging
        val similarPosts = postVectorRepository.findSimilarPostsPaged(
            embeddingStr, sources, pageable.pageSize, pageable.offset
        )
        val totalCount = postVectorRepository.countSimilarPosts(embeddingStr, sources)

        // 3. Get full Post entities by IDs (maintain similarity order)
        val postIds = similarPosts.map { it.postId }
        val similarityMap = similarPosts.associate { it.postId to it.similarity }
        val posts = postRepository.findAllById(postIds).associateBy { it.postId }

        // 4. Map to PostResponse maintaining similarity order
        val postResponses = if (isLogin()) {
            val bookmarked = bookmarkRepository.existsByPostIds(authUserId(), postIds)
            val liked = likeRepository.existsByPostIds(authUserId(), postIds)
            val commented = commentRepository.existsByPostIds(authUserId(), postIds)

            postIds.mapNotNull { id ->
                posts[id]?.let { post ->
                    PostResponse.of(
                        post = post,
                        metadata = metadataService.getMetadata(post.source),
                        bookmarked = bookmarked[id] == true,
                        liked = liked[id] == true,
                        commented = commented[id] == true,
                        similarity = similarityMap[id],
                    )
                }
            }
        } else {
            postIds.mapNotNull { id ->
                posts[id]?.let { post ->
                    PostResponse.ofOnlyPost(
                        post = post,
                        metadata = metadataService.getMetadata(post.source),
                        similarity = similarityMap[id],
                    )
                }
            }
        }

        return PageImpl(postResponses, pageable, totalCount)
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