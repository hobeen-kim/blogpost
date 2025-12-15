package com.hobeen.inserter.adapter.out.persistence.repository

import com.hobeen.inserter.adapter.out.persistence.entity.PostTag
import com.hobeen.inserter.adapter.out.persistence.entity.PostTagId
import org.springframework.data.jpa.repository.JpaRepository

interface PostTagRepository: JpaRepository<PostTag, PostTagId> {
}