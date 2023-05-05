package com.kiseru.asteroids.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kiseru.asteroids.client.dto.Message
import com.kiseru.asteroids.client.dto.Token
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.*
import java.net.Socket
import java.util.*

private const val EXIT_COMMAND = "exit"

private const val FINISH_COMMAND = "finish"

private const val HOST = "localhost"

private const val PORT = 6501

private val log = LoggerFactory.getLogger("AsteroidsSimpleClient")

suspend fun main(): Unit = coroutineScope {
    val socket = createSocket(HOST, PORT)
    val inputStream = socket.awaitInputStream()
    val reader = BufferedReader(InputStreamReader(inputStream))
    val outputStream = socket.awaitOutputStream()
    val writer = PrintWriter(outputStream, true)
    val token = authorize(reader, writer)
    launch {
        startReceiver(reader)
    }
    launch {
        startSender(writer, token)
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

suspend fun authorize(reader: BufferedReader, writer: PrintWriter): String {
    for (i in 1..2) {
        val welcomeMessage = reader.awaitReadLine()
        println(welcomeMessage)
    }
    val username = userInputFlow()
        .take(1)
        .single()
    writer.awaitPrintln(username)
    val response = reader.awaitReadLine()
    val objectMapper = jacksonObjectMapper()
    return objectMapper.readValue(response, Token::class.java).token
}

suspend fun startSender(writer: PrintWriter, token: String) = writer.use {
    val objectMapper = jacksonObjectMapper()
    userInputFlow()
        .onStart { log.info("Sender started") }
        .collect {
            val message = Message(token, it)
            val text = objectMapper.writeValueAsString(message)
            writer.awaitPrintln(text)
        }
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

suspend fun PrintWriter.awaitPrintln(x: String): Unit = withContext(Dispatchers.IO) { println(x) }

suspend fun Scanner.awaitNextLine(): String = withContext(Dispatchers.IO) { nextLine() }
