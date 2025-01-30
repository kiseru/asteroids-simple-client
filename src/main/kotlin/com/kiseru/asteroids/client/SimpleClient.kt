package com.kiseru.asteroids.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.Socket

private const val PORT = 6501
private const val HOST = "localhost"

suspend fun main(): Unit =
    coroutineScope {
        val socket = withContext(Dispatchers.IO) { Socket(HOST, PORT) }
        launch(Dispatchers.IO) {
            val listener = ServerListener(socket.getInputStream(), System.out)
            listener.listen()
        }

        launch(Dispatchers.IO) {
            val client = ServerClient(System.`in`, socket.getOutputStream())
            client.startClient()
        }
    }
