package com.kiseru.asteroids.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.*
import java.net.Socket
import java.util.*

private const val EXIT_COMMAND = "exit"

private const val HOST = "localhost"

private const val PORT = 6501

private val log = LoggerFactory.getLogger("AsteroidsSimpleClient")

fun main() = runBlocking<Unit> {
    val socket = withContext(Dispatchers.IO) { Socket(HOST, PORT) }
    val inputStream = withContext(Dispatchers.IO) { socket.getInputStream() }
    launch() { startReceiver(inputStream) }
    launch() { startSender(socket) }
}

private suspend fun startReceiver(inputStream: InputStream) {
    log.info("Receiver started")
    BufferedReader(InputStreamReader(inputStream)).use { reader ->
        while (true) {
            try {
                val inputData = withContext(Dispatchers.IO) { reader.readLine() }
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