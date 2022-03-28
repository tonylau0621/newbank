package newbank.server;

import java.util.ArrayList;

public class Customer {

	private String userID;
	private String password;
	private String firstName;
	private String lastName;
	private String phone;
	private String email;
	private String address;
	private ArrayList<Account> accounts;

	public Customer(String userID, String password, String firstName, String lastName, String phone, String email, String address) {
		this.userID = userID;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.email = email;
		this.address = address;
		accounts = new ArrayList<>();
	}

	public ArrayList<Account> getAccounts() {
		// Deep copy
		ArrayList<Account> accountsCopy = new ArrayList<>();
		for (int i = 0; i < accounts.size(); i++) {
			accountsCopy.add(new Account(accounts.get(i).getID(), accounts.get(i).getAccount(), accounts.get(i).getAmount()));
		}
		return accountsCopy;
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

	public Account getAccount(String name){
		for (int i = 0; i < accounts.size(); i++){
			if (accounts.get(i).getAccount().equals(name)){
				return accounts.get(i);
			}
		}
		return null;
	}

	public boolean checkPassword(String password) {
		return this.password.equals(password);
	}

	// oldPassword should be correct to change password
	public boolean setPassword(String oldPassword, String newPassword) {
		if (checkPassword(oldPassword)) {
			this.password = newPassword;
			return true;
		}
		return false;
	}

	// other getters and setters
	// no getPassword to avoid password leakage
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getUserID() {
		return userID;
	}

	public String getPassword(){
		return password;
	}

}
