package com.hobeen.inserter.adapter.out.persistence.entity

import com.hobeen.inserter.domain.EnrichedMessage
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
class Post (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var postId: Long? = null,
    var title: String,
    var source: String,
    var url: String,
    var pubDate: LocalDateTime,
    var description: String,
    var thumbnail: String,

    @OneToMany(mappedBy = "post", cascade = [CascadeType.ALL], orphanRemoval = true)
    val postTags: MutableList<PostTag>
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
                postTags = mutableListOf(),
            )
       }
    }

    fun updateTag(tags: List<Tag>) {
        this.postTags.removeIf { !tags.contains(it.tag) }

        val remains = postTags.map { it.tag }
        tags.forEach {
            if (!remains.contains(it)) {
                this.postTags.add(PostTag.create(post = this, tag = it))
            }
        }
    }

    fun update(message: EnrichedMessage) {
        title = message.title
        source = message.source
        url = message.url
        pubDate = message.pubDate
        description = message.description
        thumbnail = message.thumbnail
    }
}