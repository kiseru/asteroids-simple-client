import java.io.*;
import java.net.Socket;

public class SimpleClient {
    private static final int PORT = 6501;
    private static final String HOST = "localhost";

    public static void main(String[] args) throws IOException {
        Socket s = new Socket(HOST, PORT);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        PrintWriter writer = new PrintWriter(s.getOutputStream(), true);
        new Thread(new Reader(bufferedReader)).start();
        new Thread(new Writer(writer)).start();
    }
}
