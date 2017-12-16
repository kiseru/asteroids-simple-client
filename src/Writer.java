import java.io.PrintWriter;
import java.util.Scanner;

public class Writer implements Runnable {
    private PrintWriter writer;

    public Writer(PrintWriter writer) {
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
