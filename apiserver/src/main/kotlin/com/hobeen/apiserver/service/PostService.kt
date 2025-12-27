package com.hobeen.apiserver.service

import com.hobeen.apiserver.repository.BookmarkRepository
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
) {

    fun getPosts(pageable: Pageable): Page<PostResponse> {

        val posts = postRepository.findAll(pageable)

        val postIds = posts.content.map { it.postId }

        val bookmarkCounts = bookmarkRepository.countsByPostIds(postIds)
        val likeCounts = likeRepository.countsByPostIds(postIds)

        if(isLogin()) {
            val bookmarked = bookmarkRepository.existsByPostIds(authUserId(), postIds)
            val liked = likeRepository.existsByPostIds(authUserId(), postIds)

            return posts.map {
                PostResponse.of(
                    post = it,
                    bookmarked = bookmarked[it.postId] == true,
                    bookmarkCount = bookmarkCounts[it.postId] ?: 0L,
                    liked = liked[it.postId] == true,
                    likeCount = likeCounts[it.postId] ?: 0L
                ) }
        } else {
            return posts.map { PostResponse.of(
                post = it,
                bookmarked = false,
                bookmarkCount = bookmarkCounts[it.postId] ?: 0L,
                liked = false,
                likeCount = likeCounts[it.postId] ?: 0L
            ) }
        }

    }

    fun searchPosts(search: String, pageable: Pageable): List<PostResponse> {
        return postRepository.findBySearch(search, pageable).map { PostResponse.of(it, false, 0, false, 0) }
    }
}