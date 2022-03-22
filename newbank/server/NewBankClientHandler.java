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
				//If not logged in, ask customer to login
				CommunicationService.cleanTerminal();
				if (customer == null){
					out.println("Welcome to [bank name]To login, \nPlease start by entering your login details.");
					customer = UserService.login();
				}else{
					//Show other service if logged in
					out.println("Hello, " + bank.getCustomer(customer).getFirstName() +".\n\nWhat would you like to do today? \n\n 1) Show Account\n 2) Transfer Money to other Account\n 3) Make Payment\n 4) Logout");
					String request = in.readLine();
					String message = null;
					Response response = sendRequest(customer, request);
					customer = response.getCustomer();
					message = response.getResponseMessage();
					out.println(message);
					out.println("Press enter to go back to main menu.");
					request = in.readLine();
				}
			}
		} catch (IOException | InterruptedException e) {
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
	public Response sendRequest(CustomerID customer, String request) throws IOException{
		String toSend = "";
		switch(request){
			case "1":
				toSend = "SHOWMYACCOUNTS";
				break;
			case "2":
				return UserService.move(customer);
			case "3":
				toSend = "PAY";
				break;
			case "4":
				toSend = "LOGOUT";
				break;
		}
		try {
			return bank.processRequest(customer, toSend);
		} catch (InvalidAmountException | InsufficientBalanceException | InvalidAccountException e) {
			e.printStackTrace();
			return null;
		}
	}
}
