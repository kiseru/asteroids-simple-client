package com.kiseru.asteroids.client

import java.io.InputStream
import java.io.OutputStream

class ServerListener(
    private val inputStream: InputStream,
    private val outputStream: OutputStream,
) : Runnable {

    override fun run() {
        listen()
    }

    fun listen() {
        messages()
            .forEach {
                outputStream.write(("$it\n").toByteArray())
            }
    }

    private fun messages(): Sequence<String> =
        sequence {
            val reader = inputStream.bufferedReader()
            while (true) {
                val message = reader.readLine()
                yield(message)
            }
        }
}
