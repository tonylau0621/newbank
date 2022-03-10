package newbank.server;

import java.util.ArrayList;

public class Customer {

	private ArrayList<Account> accounts;
	private String password;

	public Customer(String password, ArrayList<Account> accounts) {
		this.password = password;
		this.accounts = accounts;
	}
	
	public Customer(String password) {
		this(password, new ArrayList<>());
	}
	
	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString();
		}
		return s;
	}

	public Boolean checkPassword(String input){
		return password.equals(input);
	}

	public void addAccount(Account account) {
		accounts.add(account);		
	}
}
