package com.kiseru.asteroids.client;

import java.io.*;
import java.net.Socket;

public class SimpleClient {
    private static final int PORT = 6501;
    private static final String HOST = "localhost";

    public static void main(String[] args) throws IOException {
        try (Socket s = new Socket(HOST, PORT)) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter writer = new PrintWriter(s.getOutputStream(), true);
            Thread receiver = new Thread(new Receiver(bufferedReader));
            receiver.start();
            Thread sender = new Thread(new Sender(writer));
            sender.start();
            receiver.join();
            sender.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
