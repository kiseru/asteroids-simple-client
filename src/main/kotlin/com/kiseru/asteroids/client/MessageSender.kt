package com.kiseru.asteroids.client

interface MessageSender : AutoCloseable {

    fun send(msg: String)
}