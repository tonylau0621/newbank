package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Communication handler between the server and the client.
 * This class takes commands from the client and sends them to the server.
 */
public class CommunicationService {
    private static final int SESSION_MIL_TIMEOUT = 180 * 1000;
    private static PrintWriter out;
    private static BufferedReader in;
    private static Socket socket;
    
    /** 
     * @param s
     * @throws IOException
     */
    public static void initialCommunication(Socket s) throws IOException {
        socket = s;
        if(out == null) out = new PrintWriter(s.getOutputStream(), true);
        if(in == null)  in = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    
    /** 
     * @param message
     */
    public static void sendOut(String message) {
        out.println(message);
    }

    
    /** 
     * @return String
     * @throws IOException
     */
    public static String readIn() throws IOException, SessionTimeoutException {
        String response = "";
        try {
            response = in.readLine();
        } catch (SocketTimeoutException e) {
            throw new SessionTimeoutException();
        } catch (Exception e) {}
        return response;
    }

    public static PrintWriter getPrintWriter() { return out; }
    public static BufferedReader getBufferedReader() { return in; }

    public static void cleanTerminal(){
        out.println("clearTerminal");
    }

    
    /** 
     * @param e
     * @throws InterruptedException
     */
    public static void errorAndWait(Exception e) throws InterruptedException{
		out.println(e.getMessage());
		out.println("You will be redirected to main page in 3 seconds");
		//Wait 3 second.
		for (int i = 0; i < 3; i++){
			out.println(".");
			Thread.sleep(1000);
		}
		out.println("");
		cleanTerminal();
	}

    public static void setTimeout() throws SocketException {
        socket.setSoTimeout(SESSION_MIL_TIMEOUT);
    }

    public static void removeTimeout() throws SocketException {
        socket.setSoTimeout(0);
    }
}
