package newbank.server;

import java.util.ArrayList;

public class Customer {
	
	private ArrayList<Account> accounts;
	private String password;
	
	public Customer(String password) {
		accounts = new ArrayList<>();
		this.password = password;
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
