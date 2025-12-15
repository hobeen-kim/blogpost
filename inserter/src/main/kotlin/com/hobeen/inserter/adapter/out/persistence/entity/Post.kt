package com.hobeen.inserter.adapter.out.persistence.entity

import com.hobeen.inserter.domain.EnrichedMessage
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Post (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var postId: Long? = null,
    val title: String,
    val source: String,
    val url: String,
    val pubDate: LocalDateTime,
    val description: String,
    val thumbnail: String,

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    val tags: MutableList<PostTag>
) {
    companion object {
        fun create(message: EnrichedMessage): Post {
            return Post(
                title = message.title,
                source = message.source,
                url = message.url,
                pubDate = message.pubDate,
                description = message.description,
                thumbnail = message.thumbnail,
                tags = mutableListOf(),
            )
       }
    }
}