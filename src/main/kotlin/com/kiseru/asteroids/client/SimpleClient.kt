package com.kiseru.asteroids.client

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintWriter
import java.net.Socket
import java.util.*

private const val EXIT_COMMAND = "exit"

private const val FINISH_COMMAND = "finish"

private const val HOST = "localhost"

private const val PORT = 6501

private val log = LoggerFactory.getLogger("AsteroidsSimpleClient")

suspend fun main(): Unit = coroutineScope {
    val socket = createSocket(HOST, PORT)
    launch {
        val inputStream = socket.awaitInputStream()
        val reader = BufferedReader(InputStreamReader(inputStream))
        startReceiver(reader)
    }
    launch {
        val outputStream = socket.awaitOutputStream()
        val writer = PrintWriter(outputStream, true)
        startSender(writer)
    }
}

suspend fun startReceiver(reader: BufferedReader) = reader.use {
    serverResponses(reader)
        .onStart { log.info("Receiver started") }
        .takeWhile { it != EXIT_COMMAND && it != FINISH_COMMAND }
        .collect { println(it) }
}

suspend fun serverResponses(reader: BufferedReader): Flow<String> = flow {
    while (true) {
        emit(reader.awaitReadLine())
    }
}

suspend fun startSender(writer: PrintWriter) = writer.use {
    userInputFlow()
        .onStart { log.info("Sender started") }
        .takeWhile { it != EXIT_COMMAND }
        .collect { writer.suspendedPrintln(it) }
}


suspend fun userInputFlow(): Flow<String> = flow {
    val scanner = Scanner(System.`in`)
    while (true) {
        emit(scanner.awaitNextLine())
    }
}


suspend fun createSocket(host: String, port: Int) = withContext(Dispatchers.IO) { Socket(host, port) }

suspend fun Socket.awaitInputStream(): InputStream = withContext(Dispatchers.IO) { getInputStream() }

suspend fun Socket.awaitOutputStream(): OutputStream = withContext(Dispatchers.IO) { getOutputStream() }

suspend fun BufferedReader.awaitReadLine(): String = withContext(Dispatchers.IO) { readLine() }

suspend fun PrintWriter.suspendedPrintln(x: String): Unit = withContext(Dispatchers.IO) { println(x) }

suspend fun Scanner.awaitNextLine(): String = withContext(Dispatchers.IO) { nextLine() }
