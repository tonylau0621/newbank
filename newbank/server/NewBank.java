package newbank.server;

import java.util.HashMap;

public class NewBank {


	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	
	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}
	
	private void addTestData() {
		CustomerID bhagyID = new CustomerID("Bhagy", "bhagyPass", "Bhagy", "Brown", "07654321987", "bhagyishappy@gmail.com", "123 Wonder Street, London AB1 2YZ");
		Customer bhagy = new Customer(bhagyID);
		bhagy.addAccount(new Account("Main", 1000.0));
		customers.put(bhagy.getCustomerID().getKey(), bhagy);


		CustomerID christinaID = new CustomerID("Christina", "christinaPass");
		Customer christina = new Customer(christinaID);
		christina.addAccount(new Account("Savings", 1500.0));
		customers.put(christina.getCustomerID().getKey(), christina);

		CustomerID johnID = new CustomerID("John", "johnPass");
		Customer john = new Customer(johnID);
		john.addAccount(new Account("Checking", 250.0));
		customers.put(john.getCustomerID().getKey(), john);
	}
	
	public static NewBank getBank() {
		return bank;
	}
	
	public synchronized CustomerID checkLogInDetails(String userName, String password) {
		// some dummy code demonstrating how to check password.
		Customer customer = customers.get(userName);
		if(customer != null && customer.getCustomerID().checkPassword(password)) {
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

}
