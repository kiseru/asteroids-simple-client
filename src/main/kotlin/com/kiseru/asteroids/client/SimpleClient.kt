package com.kiseru.asteroids.client

import com.kiseru.asteroids.client.impl.MessageReceiverImpl
import com.kiseru.asteroids.client.impl.MessageSenderImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.io.InputStream
import java.io.OutputStream
import java.net.Socket
import java.util.*

private const val EXIT_COMMAND = "exit"

private const val HOST = "localhost"

private const val PORT = 6501

private val log = LoggerFactory.getLogger("AsteroidsSimpleClient")

suspend fun main(): Unit = coroutineScope {
    val socket = withContext(Dispatchers.IO) { Socket(HOST, PORT) }
    launch {
        val inputStream = withContext(Dispatchers.IO) { socket.getInputStream() }
        val receiver = createReceiver(inputStream)
        startReceiver(receiver)
    }
    launch {
        val outputStream = withContext(Dispatchers.IO) { socket.getOutputStream() }
        val sender = createSender(outputStream)
        startSender(sender)
    }
}

suspend fun startReceiver(messageReceiver: MessageReceiver) = messageReceiver.use {
    log.info("Receiver started")
    flow {
        while (true) {
            emit(messageReceiver.receive())
        }
    }
        .takeWhile { it != null && it != EXIT_COMMAND }
        .collect { println(it) }
}

suspend fun startSender(messageSender: MessageSender) = messageSender.use {
    log.info("Sender started")
    val scanner = Scanner(System.`in`)
    while (true) {
        val text = withContext(Dispatchers.IO) { scanner.nextLine() }
        messageSender.send(text)
        if (text == EXIT_COMMAND) {
            break
        }
    }
}

fun createReceiver(inputStream: InputStream): MessageReceiver = MessageReceiverImpl(inputStream)

fun createSender(outputStream: OutputStream): MessageSender = MessageSenderImpl(outputStream)

