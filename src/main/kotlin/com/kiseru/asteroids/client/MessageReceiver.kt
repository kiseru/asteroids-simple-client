package com.kiseru.asteroids.client

interface MessageReceiver : AutoCloseable {

    suspend fun receive(): String?
}