package com.kiseru.asteroids.client

import java.io.BufferedReader
import java.io.IOException

class Receiver(private val reader: BufferedReader) : Runnable {

    override fun run() {
        while (true) {
            try {
                val inputData = reader.readLine()
                println(inputData)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }
        }
    }
}
