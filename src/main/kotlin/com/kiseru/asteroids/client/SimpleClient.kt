package com.kiseru.asteroids.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.net.Socket

private const val PORT = 6501
private const val HOST = "localhost"

suspend fun main(): Unit =
    coroutineScope {
        val socket = connectToServer()
        launch(Dispatchers.IO) {
            val listener = ServerListener(socket.getInputStream(), System.out)
            listener.listen()
        }

        launch(Dispatchers.IO) {
            val client = ServerClient(System.`in`, socket.getOutputStream())
            client.startClient()
        }
    }

private suspend fun connectToServer(): Socket {
    while (true) {
        try {
            println("Trying to connect to server...")
            return withContext(Dispatchers.IO) { Socket(HOST, PORT) }
        } catch (e: ConnectException) {
            println("Failed to connect to server. Next attempt in 10 seconds.")
            delay(10000)
        }
    }
}
