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
		bank = NewBank.getBank();
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out = new PrintWriter(s.getOutputStream(), true);
	}
	
	public void run() {
		//Clear Client Terminal
		clearTerminal();
		//Login
		CustomerID customer = null;
		// keep getting requests from the client and processing them
		try {
			while (true) {
				//Landing Screen
				landingScreen();
				customer = login();
				// if the user is authenticated then get requests from the user and process them
				if (customer != null) {
					out.println("Hello, " + bank.getCustomer(customer).getFirstName() +".\n\nWhat would you like to do today? \n\n 1) Show Account\n 2) Transfer Money to other Account\n 3) Make Payment");
					while(true) {
						String request = in.readLine();
						String response = "";
						System.out.println("Request from " + customer.getKey());
						switch(request) {
							case "MOVE" :
								request = runMove(customer);
								break;
						}
						response = bank.processRequest(customer, request);
						out.println(response);
					}
				}
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		} finally {
			out.println("finally");
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}

	private String runMove(CustomerID customer){
		try{
			out.println("From:");
			String from = in.readLine();
			out.println("To:");
			String to = in.readLine();
			out.println("Amount:");
			String amount = in.readLine();
			if (!from.contains(" ") && !to.contains(" ") && !amount.contains(" ")) {
				return "MOVE " + amount + " " + from + " " + to;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	private void clearTerminal(){
		out.println("clearTerminal");
	}

	private void landingScreen(){
		out.println("Welcome to [bank name]To login, \nPlease start by entering your login details.");
	}

	private CustomerID login() throws InterruptedException{
		CustomerID customer = null;
		try {
			// ask for user name
			out.println("Enter Username");
			String userName = in.readLine();
			// ask for password
			out.println("Enter Password");
			String password = in.readLine();
			out.println("Checking Details...");
			try {
				customer = bank.checkLogInDetails(userName, password);
			} catch (InvalidUserNameException | InvalidPasswordException e) {
				out.println("Log In Failed");
				errorAndWait(e);
			}
		}catch (IOException e) {
			e.printStackTrace();
		}
		return customer;
	}

	private void errorAndWait(Exception e) throws InterruptedException{
		out.println(e.getMessage());
		out.println("You will be redirected to main page in 3 seconds");
		//Wait 3 second.
		for (int i = 0; i < 3; i++){
			out.println(".");
			Thread.sleep(1000);
		}
		out.println("");
		clearTerminal();
	}
}
