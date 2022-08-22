package com.kiseru.asteroids.client.impl

import com.kiseru.asteroids.client.MessageSender
import java.io.OutputStream
import java.io.PrintWriter

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