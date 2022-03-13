package newbank.server;

import java.util.HashMap;

public class NewBank {

	//Testing
	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}
	
	private void addTestData() {
		Customer bhagy = new Customer();
		bhagy.addAccount(new Account("Main", 1000.0));
		customers.put("Bhagy", bhagy);
		
		Customer christina = new Customer();
		christina.addAccount(new Account("Savings", 1500.0));
		customers.put("Christina", christina);
		
		Customer john = new Customer();
		john.addAccount(new Account("Checking", 250.0));
		customers.put("John", john);
	}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		if(customers.containsKey(userName)) {
			return new CustomerID(userName);
		}
		return null;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) {
		if(customers.containsKey(customer.getKey())) {
			switch(request) {
			case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
			default : return "FAIL";
			}
		}
		return "FAIL";
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

	// payAmount: Takes two accounts and an amount as input
	public static void payAmount(Customer receivingAccount, Customer payingAccount, int Amount) {
		// getBalance checks the current balance for the payingAccount Customer
		int balance = receivingAccount.getBalance();
		if(balance > Amount) {
			// setBalance changes the current balance for the payingAccount Customer
			receivingAccount.setBalance((balance-Amount));
			new_balance = receivingAccount.getBalance()
			System.out.println("Payment was successful. New Balance:");
			System.out.println(new_balance);
		}
		else {
			System.out.println("Insufficient balance.");
		}

	}

}
