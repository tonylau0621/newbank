package newbank.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Class handling client connections to the bank server.
 * This is a runnable class that connects to the server and sends commands to it.
 * 
 */

public class ExampleClient extends Thread{

	private Socket server;
	private PrintWriter bankServerOut;
	private BufferedReader userInput;
	private Thread bankServerResponseThread;
	
	public ExampleClient(String ip, int port) throws UnknownHostException, IOException {
		server = new Socket(ip,port);
		userInput = new BufferedReader(new InputStreamReader(System.in)); 
		bankServerOut = new PrintWriter(server.getOutputStream(), true); 
		
		bankServerResponseThread = new Thread() {
			private BufferedReader bankServerIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
			public void run() {
				try {
					while(true) {
						String response = bankServerIn.readLine();
						if (response.equals("clearTerminal")){
							clearTerminal();
							continue;
						}
						System.out.println(response);
					}
				} catch (IOException e) {
					e.printStackTrace();
					return;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					Thread.currentThread().interrupt();
				}
			}
		};
		bankServerResponseThread.start();
	}

	public void run() {
		while(true) {
			try {
				while(true) {
					String command = userInput.readLine();
					bankServerOut.println(command);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	/** 
	 * Connects to the server when instantiated.
	 * 
	 * @param args
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
		new ExampleClient("localhost",14002).start();
	}

	
	/** 
	 * Clears the terminal screen.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 */
	//Clear the client terminal.
	private void clearTerminal() throws IOException, InterruptedException{
		final String operatingSystem = System.getProperty("os.name");

		if (operatingSystem.contains("Windows")) {
			ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
			Process startProcess = pb.inheritIO().start();
			startProcess.waitFor();
		}
		else {
			ProcessBuilder pb = new ProcessBuilder("clear");
			Process startProcess = pb.inheritIO().start();
			startProcess.waitFor();
		}
	}
}
