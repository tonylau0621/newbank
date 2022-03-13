package newbank.server;

import java.util.ArrayList;

public class Customer {

	private CustomerID customerID;
	private ArrayList<Account> accounts;

	public Customer(CustomerID customerID, ArrayList<Account> accounts) {
		this.customerID = customerID;
		this.accounts = accounts;
	}

	public Customer(CustomerID customerID) {
		this(customerID, new ArrayList<>());
	}

	public CustomerID getCustomerID() {
		return customerID;
	}
	
	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString();
		}
		return s;
	}

	public void addAccount(Account account) {
		accounts.add(account);		
	}
}
