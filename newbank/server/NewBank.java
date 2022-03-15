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
			//TODO: Talk about how to communicate with the handler.
			default : return "FAIL";
			}
		}
		return "FAIL";
	}

	public String moveAmount(double amount, String from, String to, CustomerID customer){
		//Account does not exist
		Customer target = customers.get(customer.getKey());
		if (!target.accountsToString().contains(from) || !target.accountsToString().contains(to)){
			//return "Account does not exist";
			return "FAIL";
		}
		//Not enough balance
		if (target.getAccount(from).getAmount() < amount){
			//return "Not enough amount in your account";
			return "FAIL";
		}
		//Update the amount and return success
		target.getAccount(from).updateBalance(-amount);
		target.getAccount(to).updateBalance(amount);
		return "SUCCESS";
	}
	
	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

}
