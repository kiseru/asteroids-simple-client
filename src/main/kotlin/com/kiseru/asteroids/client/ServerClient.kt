package com.kiseru.asteroids.client

import java.io.InputStream
import java.io.OutputStream
import java.util.Scanner


class ServerClient(
    private val inputStream: InputStream,
    private val outputStream: OutputStream,
) : Runnable {

    override fun run() {
        startClient()
    }

    fun startClient() {
        val writer = outputStream.writer()
        userInput()
            .forEach {
                writer.appendLine(it)
                writer.flush()
            }
    }


    private fun userInput(): Sequence<String> =
        sequence {
            val scanner = Scanner(inputStream)
            while (true) {
                val command = scanner.nextLine()
                yield(command)
            }
        }
}
