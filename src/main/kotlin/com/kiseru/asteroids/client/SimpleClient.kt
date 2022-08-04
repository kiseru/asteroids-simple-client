package com.kiseru.asteroids.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.*

private const val HOST = "localhost"

private const val PORT = 6501

fun main() = runBlocking<Unit> {
    val socket = withContext(Dispatchers.IO) { Socket(HOST, PORT) }

    launch() { startReceiver(socket) }

    launch() { startSender(socket) }
}

private suspend fun startReceiver(socket: Socket) {
    println("Receiver started")
    val inputStream = withContext(Dispatchers.IO) { socket.getInputStream() }
    BufferedReader(InputStreamReader(inputStream)).use { reader ->
        while (true) {
            try {
                val inputData: String = withContext(Dispatchers.IO) { reader.readLine() }
                println(inputData)
            } catch (e: IOException) {
                break
            }
        }
    }
}

private suspend fun startSender(socket: Socket) {
    println("Sender started")
    val outputStream = withContext(Dispatchers.IO) { socket.getOutputStream() }
    val writer = PrintWriter(outputStream, true)
    val scanner = Scanner(System.`in`)
    while (true) {
        val text = withContext(Dispatchers.IO) { scanner.nextLine() }
        if ("exit" == text) {
            break
        }

        writer.println(text)
    }

    withContext(Dispatchers.IO) { socket.close() }
}