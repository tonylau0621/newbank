package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class CommunicationService {
    private static PrintWriter out;
    private static BufferedReader in;

    
    /** 
     * @param s
     * @throws IOException
     */
    public static void initialCommunication(Socket s) throws IOException {
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
    public static String readIn() throws IOException {
        return in.readLine();
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
}
