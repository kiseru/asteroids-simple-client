package com.kiseru.asteroids.client

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket

private const val PORT = 6501
private const val HOST = "localhost"

fun main() {
    Socket(HOST, PORT).use {
        val bufferedReader = BufferedReader(InputStreamReader(it.getInputStream()))
        val writer = PrintWriter(it.getOutputStream(), true)
        val receiver = Thread(Receiver(bufferedReader))
        receiver.start()
        val sender = Thread(Sender(writer))
        sender.start()
        receiver.join()
        sender.join()
    }
}
