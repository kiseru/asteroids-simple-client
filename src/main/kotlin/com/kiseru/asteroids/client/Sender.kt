package com.kiseru.asteroids.client

import java.io.PrintWriter
import java.util.Scanner


class Sender(private val writer: PrintWriter) : Runnable {
    override fun run() {
        val sc = Scanner(System.`in`)
        while (true) {
            try {
                val text = sc.nextLine()
                writer.println(text)
            } catch (ex: Exception) {
                System.exit(1)
            }
        }
    }
}
