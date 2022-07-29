package com.kiseru.asteroids.client;


import java.io.PrintWriter;
import java.util.Scanner;

public class Sender implements Runnable {
    private PrintWriter writer;

    public Sender(PrintWriter writer) {
        this.writer = writer;
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            try {
                String text = sc.nextLine();
                writer.println(text);
            } catch (Exception ex) {
                System.exit(1);
            }
        }
    }
}
