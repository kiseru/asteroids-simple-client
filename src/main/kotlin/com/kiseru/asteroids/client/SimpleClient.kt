package com.kiseru.asteroids.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.*

private const val EXIT_COMMAND = "exit"

private const val HOST = "localhost"

private const val PORT = 6501

private val log = LoggerFactory.getLogger("AsteroidsSimpleClient")

fun main() = runBlocking<Unit> {
    val socket = withContext(Dispatchers.IO) { Socket(HOST, PORT) }
    launch() { startReceiver(socket) }
    launch() { startSender(socket) }
}

private suspend fun startReceiver(socket: Socket) {
    log.info("Receiver started")
    val inputStream = withContext(Dispatchers.IO) { socket.getInputStream() }
    BufferedReader(InputStreamReader(inputStream)).use { reader ->
        while (true) {
            try {
                val inputData: String = withContext(Dispatchers.IO) { reader.readLine() }
                if (inputData == EXIT_COMMAND) {
                    break
                }

                println(inputData)
            } catch (e: IOException) {
                break
            }
        }
    }
}

private suspend fun startSender(socket: Socket) {
    log.info("Sender started")
    val outputStream = withContext(Dispatchers.IO) { socket.getOutputStream() }
    PrintWriter(outputStream, true).use { writer ->
        val scanner = Scanner(System.`in`)
        while (true) {
            val text = withContext(Dispatchers.IO) { scanner.nextLine() }
            writer.println(text)
            if (text == EXIT_COMMAND) {
                break
            }
        }
    }
}