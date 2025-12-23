package com.hobeen.metadatagenerator.adapter.out.persistence

import org.springframework.data.jpa.repository.JpaRepository

interface ParsePropsRepository: JpaRepository<ParsePropsEntity, String> {

    fun getParsePropsBySource(source: String): ParsePropsEntity?
}