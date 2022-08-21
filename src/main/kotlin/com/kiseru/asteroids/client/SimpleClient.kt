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
    while (true) {
        try {
            val inputData = messageReceiver.receive() ?: break
            if (inputData == EXIT_COMMAND) {
                break
            }

            println(inputData)
        } catch (e: IOException) {
            break
        }
    }
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

interface MessageReceiver : AutoCloseable {

    suspend fun receive(): String?
}

interface MessageSender : AutoCloseable {

    fun send(msg: String)
}

class MessageReceiverImpl(
    inputStream: InputStream,
) : MessageReceiver {

    private val reader = BufferedReader(InputStreamReader(inputStream))

    override suspend fun receive(): String? = withContext(Dispatchers.IO) {
        reader.readLine()
    }

    override fun close() {
        reader.close()
    }
}

class MessageSenderImpl(
    outputStream: OutputStream,
) : MessageSender {

    private val writer = PrintWriter(outputStream)

    override fun send(msg: String) {
        writer.println(msg)
        writer.flush()
    }

    override fun close() {
        writer.close()
    }
}