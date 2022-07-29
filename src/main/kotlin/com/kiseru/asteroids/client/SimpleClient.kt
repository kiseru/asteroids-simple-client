package com.kiseru.asteroids.client

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.*

private const val HOST = "localhost"

private const val PORT = 6501

fun main() = runBlocking<Unit>(Dispatchers.IO) {
    val socket = Socket(HOST, PORT)

    launch(Dispatchers.IO) {
        startReceiver(socket)
    }

    launch(Dispatchers.IO) {
        startSender(socket)
    }
}

private fun startReceiver(socket: Socket) {
    BufferedReader(InputStreamReader(socket.getInputStream())).use { reader ->
        while (true) {
            try {
                val inputData: String = reader.readLine()
                println(inputData)
            } catch (e: IOException) {
                break
            }
        }
    }
}

private fun startSender(socket: Socket) {
    val writer = PrintWriter(socket.getOutputStream(), true)
    val scanner = Scanner(System.`in`)
    while (true) {
        val text = scanner.nextLine()
        if ("exit" == text) {
            break
        }

        writer.println(text)
    }

    socket.close()
}
