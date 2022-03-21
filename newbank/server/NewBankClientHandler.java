package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NewBankClientHandler extends Thread {
	
	private NewBank bank;
	private BufferedReader in;
	private PrintWriter out;
	
	
	public NewBankClientHandler(Socket s) throws IOException {
		CommunicationService.initialCommunication(s);
		bank = NewBank.getBank();
		in = CommunicationService.getBufferedReader();
		out = CommunicationService.getPrintWriter();
	}
	
	public void run() {
		// keep getting requests from the client and processing them
		try {
			CustomerID customer = null;
			while(true) {
				out.println("What do you want to do?");
				String request = in.readLine();
				String message = null;
				Response response = bank.processRequest(customer, request);
				customer = response.getCustomer();
				message = response.getResponseMessage();
				out.println(message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}
}
