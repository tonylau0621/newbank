package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NewBankClientHandler extends Thread{
	
	private NewBank bank;
	private BufferedReader in;
	private PrintWriter out;
	
	
	public NewBankClientHandler(Socket s) throws IOException {
		bank = NewBank.getBank();
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out = new PrintWriter(s.getOutputStream(), true);
	}
	
	public void run() {
		// keep getting requests from the client and processing them
		try {
			// ask for user name
			out.println("Enter Username");
			String userName = in.readLine();
			// ask for password
			out.println("Enter Password");
			String password = in.readLine();
			out.println("Checking Details...");
			// authenticate user and get customer ID token from bank for use in subsequent requests
			CustomerID customer = bank.checkLogInDetails(userName, password);
			// if the user is authenticated then get requests from the user and process them 
			if(customer != null) {
				out.println("Log In Successful. What do you want to do?");
				while(true) {
					String request = in.readLine();
					System.out.println("Request from " + customer.getKey());
					String response = "";
					switch (request){
						case "SHOWMYACCOUNTS" : {
							response = bank.processRequest(customer, request, null);
							break;
						}
						case "MOVE" : 
						{
							response = runMove(customer);
							break;
						}
					}
					out.println(response);
				}
			}
			else {
				out.println("Log In Failed");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}

	public String runMove(CustomerID customer){
		try{
			out.println("From:");
			String from = in.readLine();
			out.println("To:");
			String to = in.readLine();
			out.println("Amount:");
			double amount = 0;
			try{
				amount = Double.valueOf(in.readLine());
			}catch(NumberFormatException ne){
				ne.printStackTrace();
			}
			String[] parameters = new String[3];
			parameters[0] = String.valueOf(amount);
			parameters[1] = from;
			parameters[2] = to;
			return bank.processRequest(customer, "MOVE", parameters);
		}catch (IOException e) {
			e.printStackTrace();
			return "FAIL";
		}
	}
}
