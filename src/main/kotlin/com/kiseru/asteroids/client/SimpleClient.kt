package com.kiseru.asteroids.client

import java.net.Socket
import kotlin.concurrent.thread

private const val PORT = 6501
private const val HOST = "localhost"

fun main() {
    val socket = Socket(HOST, PORT)
    thread {
        val listener = ServerListener(socket.getInputStream(), System.out)
        listener.listen()
    }

    thread {
        val client = ServerClient(System.`in`, socket.getOutputStream())
        client.startClient()
    }
}
