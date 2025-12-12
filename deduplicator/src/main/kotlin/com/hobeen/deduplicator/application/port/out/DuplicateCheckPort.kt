package com.hobeen.deduplicator.application.port.out

interface DuplicateCheckPort {

    fun checkAndSave(url: String): Boolean

    fun delete(url: String)
}