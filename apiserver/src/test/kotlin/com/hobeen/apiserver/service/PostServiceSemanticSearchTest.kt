package com.hobeen.apiserver.service

import com.hobeen.apiserver.entity.Post
import com.hobeen.apiserver.repository.*
import com.hobeen.apiserver.service.dto.SourceMetadataCache
import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageRequest
import org.assertj.core.api.Assertions.assertThat
import java.time.LocalDateTime

class PostServiceSemanticSearchTest {

    private lateinit var postService: PostService
    private val postRepository: PostRepository = mockk()
    private val bookmarkRepository: BookmarkRepository = mockk()
    private val likeRepository: LikeRepository = mockk()
    private val commentRepository: CommentRepository = mockk()
    private val metadataService: MetadataService = mockk()
    private val postVectorRepository: PostVectorRepository = mockk()
    private val embeddingService: EmbeddingService = mockk()

    @BeforeEach
    fun setUp() {
        postService = PostService(
            postRepository = postRepository,
            bookmarkRepository = bookmarkRepository,
            likeRepository = likeRepository,
            commentRepository = commentRepository,
            metadataService = metadataService,
            postVectorRepository = postVectorRepository,
            embeddingService = embeddingService,
        )
    }

    @Test
    @DisplayName("시맨틱 검색 - 임베딩 변환 후 유사도 순으로 결과 반환")
    fun semanticSearchReturnsSimilarityOrdered() {
        // given
        val query = "kafka 메시지 큐"
        val pageable = PageRequest.of(0, 10)
        val embedding = listOf(0.1, 0.2, 0.3)
        val embeddingStr = "[0.1, 0.2, 0.3]"

        every { embeddingService.embed(query) } returns embedding
        every { postVectorRepository.findSimilarPostsPaged(embeddingStr, null, 10, 0) } returns listOf(
            SimilarPost(1L, "Kafka 기초", "toss", "http://url1", "content1", 0.95),
            SimilarPost(2L, "메시지 큐 설계", "woowahan", "http://url2", "content2", 0.80),
        )
        every { postVectorRepository.countSimilarPosts(embeddingStr, null) } returns 2L

        // Create mock Post entities — need to check Post constructor
        val post1 = mockk<Post>(relaxed = true)
        every { post1.postId } returns 1L
        every { post1.title } returns "Kafka 기초"
        every { post1.source } returns "toss"
        every { post1.url } returns "http://url1"
        every { post1.pubDate } returns LocalDateTime.of(2024, 1, 1, 0, 0)
        every { post1.description } returns "desc1"
        every { post1.thumbnail } returns "thumb1"
        every { post1.tags } returns mutableListOf()
        every { post1.abstractedContent } returns "abstract1"
        every { post1.bookmarks } returns mutableListOf()
        every { post1.likes } returns mutableListOf()
        every { post1.comments } returns mutableListOf()

        val post2 = mockk<Post>(relaxed = true)
        every { post2.postId } returns 2L
        every { post2.title } returns "메시지 큐 설계"
        every { post2.source } returns "woowahan"
        every { post2.url } returns "http://url2"
        every { post2.pubDate } returns LocalDateTime.of(2024, 1, 2, 0, 0)
        every { post2.description } returns "desc2"
        every { post2.thumbnail } returns "thumb2"
        every { post2.tags } returns mutableListOf()
        every { post2.abstractedContent } returns "abstract2"
        every { post2.bookmarks } returns mutableListOf()
        every { post2.likes } returns mutableListOf()
        every { post2.comments } returns mutableListOf()

        every { postRepository.findAllById(listOf(1L, 2L)) } returns listOf(post1, post2)
        every { metadataService.getMetadata(any()) } returns SourceMetadataCache(ko = "테스트")

        // SecurityContext를 모킹하지 않으면 isLogin()이 false 반환
        // → non-login path (ofOnlyPost) 실행

        // when
        val result = postService.getPostsSemantic(query, null, pageable)

        // then
        assertThat(result.content).hasSize(2)
        assertThat(result.content[0].title).isEqualTo("Kafka 기초")
        assertThat(result.content[0].similarity).isEqualTo(0.95)
        assertThat(result.content[1].title).isEqualTo("메시지 큐 설계")
        assertThat(result.content[1].similarity).isEqualTo(0.80)
        assertThat(result.totalElements).isEqualTo(2L)

        verify { embeddingService.embed(query) }
        verify { postVectorRepository.findSimilarPostsPaged(embeddingStr, null, 10, 0) }
    }

    @Test
    @DisplayName("시맨틱 검색 - 소스 필터링 적용")
    fun semanticSearchWithSourceFilter() {
        // given
        val query = "react"
        val sources = listOf("toss")
        val pageable = PageRequest.of(0, 5)
        val embedding = listOf(0.1, 0.2)
        val embeddingStr = "[0.1, 0.2]"

        every { embeddingService.embed(query) } returns embedding
        every { postVectorRepository.findSimilarPostsPaged(embeddingStr, sources, 5, 0) } returns listOf(
            SimilarPost(3L, "React Native 라우팅", "toss", "http://url3", "content3", 0.90),
        )
        every { postVectorRepository.countSimilarPosts(embeddingStr, sources) } returns 1L

        val post3 = mockk<Post>(relaxed = true)
        every { post3.postId } returns 3L
        every { post3.source } returns "toss"
        every { post3.tags } returns mutableListOf()
        every { post3.bookmarks } returns mutableListOf()
        every { post3.likes } returns mutableListOf()
        every { post3.comments } returns mutableListOf()

        every { postRepository.findAllById(listOf(3L)) } returns listOf(post3)
        every { metadataService.getMetadata(any()) } returns SourceMetadataCache(ko = "토스")

        // when
        val result = postService.getPostsSemantic(query, sources, pageable)

        // then
        assertThat(result.content).hasSize(1)
        assertThat(result.content[0].similarity).isEqualTo(0.90)
        verify { postVectorRepository.findSimilarPostsPaged(embeddingStr, sources, 5, 0) }
    }

    @Test
    @DisplayName("시맨틱 검색 - 페이징 offset 계산")
    fun semanticSearchPagingOffset() {
        // given
        val query = "kubernetes"
        val pageable = PageRequest.of(2, 10) // page 2, size 10 → offset 20
        val embedding = listOf(0.5)
        val embeddingStr = "[0.5]"

        every { embeddingService.embed(query) } returns embedding
        every { postVectorRepository.findSimilarPostsPaged(embeddingStr, null, 10, 20) } returns emptyList()
        every { postVectorRepository.countSimilarPosts(embeddingStr, null) } returns 25L
        every { postRepository.findAllById(emptyList()) } returns emptyList()

        // when
        val result = postService.getPostsSemantic(query, null, pageable)

        // then
        assertThat(result.content).isEmpty()
        assertThat(result.totalElements).isEqualTo(25L)
        verify { postVectorRepository.findSimilarPostsPaged(embeddingStr, null, 10, 20) }
    }
}
