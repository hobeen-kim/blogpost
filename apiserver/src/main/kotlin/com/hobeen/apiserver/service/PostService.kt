package com.hobeen.apiserver.service

import com.hobeen.apiserver.repository.BookmarkRepository
import com.hobeen.apiserver.repository.CommentRepository
import com.hobeen.apiserver.repository.LikeRepository
import com.hobeen.apiserver.repository.PostRepository
import com.hobeen.apiserver.service.dto.PostResponse
import com.hobeen.apiserver.util.auth.authUserId
import com.hobeen.apiserver.util.auth.isLogin
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class PostService(
    private val postRepository: PostRepository,
    private val bookmarkRepository: BookmarkRepository,
    private val likeRepository: LikeRepository,
    private val commentRepository: CommentRepository,

    private val sourceService: SourceService,
) {

    fun getPosts(pageable: Pageable): Page<PostResponse> {

        val posts = postRepository.findAll(pageable)

        val postIds = posts.content.map { it.postId }

        if(isLogin()) {
            val bookmarked = bookmarkRepository.existsByPostIds(authUserId(), postIds)
            val liked = likeRepository.existsByPostIds(authUserId(), postIds)
            val commented = commentRepository.existsByPostIds(authUserId(), postIds)

            return posts.map {
                PostResponse.of(
                    post = it,
                    metadata = sourceService.getMetadata(it.source),
                    bookmarked = bookmarked[it.postId] == true,
                    liked = liked[it.postId] == true,
                    commented = commented[it.postId] == true,
                ) }
        } else {
            return posts.map { PostResponse.of(
                post = it,
                metadata = sourceService.getMetadata(it.source),
                bookmarked = false,
                liked = false,
                commented = false,
            ) }
        }
    }

    fun searchPosts(search: String, pageable: Pageable): List<PostResponse> {
        return postRepository.findBySearch(search, pageable).map { PostResponse.ofOnlyPost(it, metadata = sourceService.getMetadata(it.source)) }
    }
}