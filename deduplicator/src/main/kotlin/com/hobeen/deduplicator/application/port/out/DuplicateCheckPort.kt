package com.hobeen.deduplicator.application.port.out

interface DuplicateCheckPort {

    fun checkAndSave(url: String): Boolean

    fun addDuplicateSet(urls: List<String>)

    fun delete(url: String)
}