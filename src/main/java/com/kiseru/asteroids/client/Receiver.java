package com.kiseru.asteroids.client;

import java.io.BufferedReader;
import java.io.IOException;

public class Receiver implements Runnable {
    private BufferedReader reader;

    public Receiver(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String inputData = reader.readLine();
                System.out.println(inputData);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
