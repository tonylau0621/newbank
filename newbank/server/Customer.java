package newbank.server;

import newbank.server.loan.AvailableLoan;
import newbank.server.loan.Loan;

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
	// for mirco-loan
	private ArrayList<AvailableLoan> availableLoans;
	private ArrayList<Loan> lentLoans;
	private ArrayList<Loan> borrowedLoans;

	public Customer(String userID, String password, String firstName, String lastName, String phone, String email, String address) {
		this.userID = userID;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.email = email;
		this.address = address;
		accounts = new ArrayList<>();
		availableLoans = new ArrayList<>();
		lentLoans = new ArrayList<>();
		borrowedLoans = new ArrayList<>();
	}

	public ArrayList<Account> getAccounts() {
		// Deep copy
		ArrayList<Account> accountsCopy = new ArrayList<>();
		for (int i = 0; i < accounts.size(); i++) {
			accountsCopy.add(new Account(accounts.get(i).getAccount(), accounts.get(i).getAmount()));
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

	public String showLoanDetails() {
		String s = "";
		double totalAvailableLoan = getTotalAvailableLoans();
		double totalLentLoan = getTotalLentLoans();
		double totalBorrowedLoan = getTotalBorrowedLoans();
		if ((totalAvailableLoan + totalLentLoan) > 0) {
			s += "Total amount in the lending account: " + (totalAvailableLoan + totalLentLoan) + ", where\n";
			s += "\tLent: " + totalLentLoan + "\n";
			s += "\tRepaid/Not lent: " + totalAvailableLoan + " (You may transfer up to this amount to other accounts.)\n";
		}
		if (totalBorrowedLoan > 0) {
			s += "You still owes other customers " + totalBorrowedLoan + ".\n";
		}
		return s;
	}

	private double getTotalAvailableLoans() {
		double total = 0;
		System.out.println("testing1");
		for (AvailableLoan aL : availableLoans) {
			total += aL.getAmount();
			System.out.println("testing2");
		}
		return total;
	}

	private double getTotalLentLoans() {
		double total = 0;
		for (Loan lL : lentLoans) {
			total += lL.getRemainingAmount();
		}
		return total;
	}

	private double getTotalBorrowedLoans() {
		double total = 0;
		for (Loan bL: borrowedLoans) {
			total += bL.getRemainingAmount();
		}
		return total;
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

	public void addAvailableLoan(AvailableLoan availableLoan) {
		availableLoans.add(availableLoan);
	}

	public void addLentLoan(Loan loan) {
		lentLoans.add(loan);
	}

	public void addBorrowedLoan(Loan loan) {
		borrowedLoans.add(loan);
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

}
