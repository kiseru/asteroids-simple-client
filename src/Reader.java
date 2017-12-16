import java.io.BufferedReader;
import java.io.IOException;

public class Reader implements Runnable {
    private BufferedReader reader;

    public Reader(BufferedReader reader) {
        this.reader = reader;
    }

    @Override
    public void run() {
        while (true) {
            try {
                String inputData = reader.readLine();
                System.out.println(inputData);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }
}
