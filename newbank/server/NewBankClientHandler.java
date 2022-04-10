package newbank.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Communication handler instantiated on each client connection.
 * Sends and receives messages from the client and server.
 */
public class NewBankClientHandler extends Thread {
	
	private NewBank bank;
	private BufferedReader in;
	private PrintWriter out;
	
	
	public NewBankClientHandler(Socket s) throws IOException {
		CommunicationService.initialCommunication(s);
		bank = NewBank.getBank();
		bank.addLoanData();
		in = CommunicationService.getBufferedReader();
		out = CommunicationService.getPrintWriter();
	}
	
	public void run() {
		// keep getting requests from the client and processing them
		CustomerID customer = null;
		try {
			String message;
			while(true) {
				try {
					//If not logged in, ask customer to login
					CommunicationService.cleanTerminal();
					Response response = null;
					if (customer == null){
						CommunicationService.removeTimeout();
						customer = welcomePage();
						CommunicationService.setTimeout();
					} else if (customer != null && customer.isAdmin()) {
						// go to admin menu
						out.println("Hello, " + customer.getKey() +".\n\nWhat would you like to do today? \n\n 1) Unlock User \n 2) Logout");
						String request = CommunicationService.readIn();
						out.println("Processing admin request...");
						response = sendAdminRequest(customer, request);
						customer = response.getCustomer();
						message = response.getResponseMessage();
						out.println(message);
						out.println("Press enter to continue");
						request = CommunicationService.readIn();
					} else {
						//Show other service if logged in
						out.println("Hello, " + bank.getCustomer(customer).getFirstName() +".\n\nWhat would you like to do today? \n\n 1) Show Account\n 2) Transfer Money to other Account\n 3) Make Payment" +
								"\n 4) Create New Account\n 5) View Transaction History\n 6) Handle Loans\n 7) Logout");
						String request = CommunicationService.readIn();
						response = sendRequest(customer, request);
						customer = response.getCustomer();
						message = response.getResponseMessage();
						out.println(message);
						out.println("Press enter to go back to main menu.");
						request = CommunicationService.readIn();
					}
				} catch (SessionTimeoutException e) {
					customer = null;
					CommunicationService.sendOut(e.getMessage());
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

	
	/** 
	 * @param customer
	 * @param request
	 * @return Response
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Response sendRequest(CustomerID customer, String request) throws IOException, InterruptedException, SessionTimeoutException {
		String toSend = "";
		switch(request){
			case "1":
				toSend = "SHOWMYACCOUNTS";
				break;
			case "2":
				return UserService.move(customer);
			case "3":
				return UserService.pay(customer);
			case "4":
				return UserService.newAccount(customer);
			case "5":
				toSend = "TRANSACTIONRECORD";
				break;
			case "6":
				return UserService.loan(customer);
			case "7":
				toSend = "LOGOUT";
				break;
			case "L2":
				return UserService.newCustomer();
			default:
				toSend = "";
		}
		try {
			return bank.processRequest(customer, toSend);
		} catch (InvalidAmountException | InsufficientBalanceException | InvalidAccountException | InvalidUserNameException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	/** 
	 * @param customer
	 * @param request
	 * @return Response
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public Response sendAdminRequest(CustomerID customer, String request) throws IOException, InterruptedException, SessionTimeoutException {
		String toSend = "";
		switch(request){
			case "1":
				toSend = "UNLOCKUSER";
				break;
			case "2":
				toSend = "LOGOUT";
				break;
			default:
				toSend = "";
		}
		try {
			return bank.processRequest(customer, toSend);
		} catch (InvalidAmountException | InsufficientBalanceException | InvalidAccountException | InvalidUserNameException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	/** 
	 * @return CustomerID
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private CustomerID welcomePage() throws IOException, InterruptedException, SessionTimeoutException {
		out.println("Welcome to [bank name]\nTo login, Please login or create new user account\n 1) Login\n 2) New User Account");
		String input = CommunicationService.readIn();
		if (input.equals("1")){
			return UserService.login();
		}
		else if (input.equals("2")){
			Response response = sendRequest(null, "L"+input);
			if (response != null){
				String message = response.getResponseMessage();
				out.println(message);
				out.println("Press enter to go back to main menu.");
				input = CommunicationService.readIn();
			}
		}
		return null;
	}
}
