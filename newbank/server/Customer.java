package newbank.server;

import java.util.ArrayList;

/**
 * Class defining a customer account, with customer specific methods.
 * The included methods relate to bank account management.
 * 
 */
public class Customer extends User{
	private ArrayList<Account> accounts;

	public Customer(String userID, String password, String firstName, String lastName, String phone, String email, String address) {
		super(userID, password, firstName, lastName, phone, email, address);
		accounts = new ArrayList<>();
	}

	
	/** 
	 * @return ArrayList<Account>
	 */
	public ArrayList<Account> getAccounts() {
		// Deep copy
		ArrayList<Account> accountsCopy = new ArrayList<>();
		for (int i = 0; i < accounts.size(); i++) {
			accountsCopy.add(new Account(accounts.get(i).getID(), accounts.get(i).getAccount(), accounts.get(i).getAmount()));
		}
		return accountsCopy;
	}

	
	/** 
	 * @return String
	 */
	public String accountsToString() {
		String s = "";
		for(Account a : accounts) {
			s += a.toString();
		}
		return s;
	}

	
	/** 
	 * @param account
	 */
	public void addAccount(Account account) {
		accounts.add(account);
	}

	
	/** 
	 * @param name
	 * @return Account
	 */
	public Account getAccount(String name){
		for (int i = 0; i < accounts.size(); i++){
			if (accounts.get(i).getAccount().equals(name)){
				return accounts.get(i);
			}
		}
		return null;
	}
}
