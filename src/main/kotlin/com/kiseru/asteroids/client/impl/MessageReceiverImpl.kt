package com.kiseru.asteroids.client.impl

import com.kiseru.asteroids.client.MessageReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

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