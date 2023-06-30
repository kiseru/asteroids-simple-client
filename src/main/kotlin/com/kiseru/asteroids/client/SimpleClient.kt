package com.kiseru.asteroids.client

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.kiseru.asteroids.client.command.CommandType
import com.kiseru.asteroids.client.auth.AuthResponseDto
import com.kiseru.asteroids.client.command.CommandResponseDto
import com.kiseru.asteroids.client.token.Token
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.*

private const val EXIT_COMMAND = "exit"

private const val FINISH_COMMAND = "finish"

private const val HOST = "localhost"

private const val PORT = 6501

private const val DEFAULT_PASSWORD = "password"

private val log = LoggerFactory.getLogger("AsteroidsSimpleClient")

@SpringBootApplication
class AsteroidSimpleClient {

    @Bean
    fun reader(socket: Socket): BufferedReader =
        BufferedReader(InputStreamReader(socket.getInputStream()))

    @Bean
    fun writer(socket: Socket): PrintWriter =
        PrintWriter(socket.getOutputStream(), true)

    @Bean
    fun socket() =
        Socket(HOST, PORT)

    @Bean
    fun webClient() =
        WebClient.create("http://localhost:8080")
}

suspend fun main(args: Array<String>): Unit = coroutineScope {
    val context = runApplication<AsteroidSimpleClient>(*args)
    val reader = context.getBean(BufferedReader::class.java)
    val writer = context.getBean(PrintWriter::class.java)
    val token = authorize(reader, writer)
    val webClient = context.getBean(WebClient::class.java)
    launch {
        startReceiver(reader)
    }
    launch {
        startSender(writer, webClient, token.username, DEFAULT_PASSWORD)
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

suspend fun authorize(reader: BufferedReader, writer: PrintWriter): AuthResponseDto {
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
    val token = objectMapper.readValue(response, Token::class.java).token
    return AuthResponseDto(username, token)
}

suspend fun startSender(
    writer: PrintWriter,
    webClient: WebClient,
    username: String,
    password: String,
) =
    writer.use {
        val inputMap = createInputMap()
        userInputFlow()
            .onStart { log.info("Sender started") }
            .map { CommandType.valueOf(it) }
            .collect {
                val handler = inputMap[it]
                if (handler != null) {
                    val response = handler(webClient, username, password)
                    println(response.result)
                    return@collect
                }

                println("Введена неизвестная команда")
            }
    }

private fun createInputMap(): Map<CommandType, suspend (WebClient, String, String) -> CommandResponseDto> =
    mapOf(
        CommandType.DOWN to ::sendDownCommand,
        CommandType.EXIT to ::sendExitCommand,
        CommandType.GO to ::sendGoCommand,
        CommandType.IS_ASTEROID to ::sendIsAsteroidCommand,
        CommandType.IS_GARBAGE to ::sendIsGarbageCommand,
        CommandType.IS_WALL to ::sendIsWallCommand,
        CommandType.LEFT to ::sendLeftCommand,
        CommandType.RIGHT to ::sendRightCommand,
        CommandType.UP to ::sendUpCommand,
    )

private suspend fun sendDownCommand(webClient: WebClient, username: String, password: String): CommandResponseDto =
    sendCommand(webClient, "api/v1/commands/down", username, password)

private suspend fun sendExitCommand(webClient: WebClient, username: String, password: String): CommandResponseDto =
    sendCommand(webClient, "api/v1/commands/exit", username, password)

private suspend fun sendGoCommand(webClient: WebClient, username: String, password: String): CommandResponseDto =
    sendCommand(webClient, "api/v1/commands/go", username, password)

private suspend fun sendIsAsteroidCommand(
    webClient: WebClient,
    username: String,
    password: String,
): CommandResponseDto =
    sendCommand(webClient, "api/v1/commands/is-asteroid", username, password)

private suspend fun sendIsGarbageCommand(webClient: WebClient, username: String, password: String): CommandResponseDto =
    sendCommand(webClient, "api/v1/commands/is-garbage", username, password)

private suspend fun sendIsWallCommand(webClient: WebClient, username: String, password: String): CommandResponseDto =
    sendCommand(webClient, "api/v1/commands/is-wall", username, password)

private suspend fun sendLeftCommand(webClient: WebClient, username: String, password: String): CommandResponseDto =
    sendCommand(webClient, "api/v1/commands/left", username, password)

private suspend fun sendRightCommand(webClient: WebClient, username: String, password: String): CommandResponseDto =
    sendCommand(webClient, "api/v1/commands/right", username, password)

private suspend fun sendUpCommand(webClient: WebClient, username: String, password: String): CommandResponseDto =
    sendCommand(webClient, "api/v1/commands/up", username, password)

private suspend fun sendCommand(webClient: WebClient, uri: String, username: String, password: String):
        CommandResponseDto =
    webClient
        .post()
        .uri(uri)
        .headers { it.setBasicAuth(username, password) }
        .awaitExchange { it.awaitBody() }

suspend fun userInputFlow(): Flow<String> = flow {
    val scanner = Scanner(System.`in`)
    while (true) {
        emit(scanner.awaitNextLine())
    }
}

suspend fun BufferedReader.awaitReadLine(): String = withContext(Dispatchers.IO) { readLine() }

suspend fun PrintWriter.awaitPrintln(x: String): Unit = withContext(Dispatchers.IO) { println(x) }

suspend fun Scanner.awaitNextLine(): String = withContext(Dispatchers.IO) { nextLine() }

