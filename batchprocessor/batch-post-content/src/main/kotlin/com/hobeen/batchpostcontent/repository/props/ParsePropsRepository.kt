package com.hobeen.batchpostcontent.repository.props

import com.hobeen.batchpostcontent.entity.props.ParseProps
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface ParsePropsRepository: JpaRepository<ParseProps, String> {
    @Query("SELECT DISTINCT p FROM ParseProps p LEFT JOIN FETCH p.nodes")
    fun findAllWithNodes(): List<ParseProps>
}
