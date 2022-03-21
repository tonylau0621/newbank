package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CommunicationService {
    private static PrintWriter out;
    private static BufferedReader in;

    public static void initialCommunication(Socket s) throws IOException {
        if(out == null) out = new PrintWriter(s.getOutputStream(), true);
        if(in == null)  in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    public static void sendOut(String message) {
        out.println(message);
    }

    public static String readIn() throws IOException {
        return in.readLine();
    }

    public static PrintWriter getPrintWriter() { return out; }
    public static BufferedReader getBufferedReader() { return in; }
}
