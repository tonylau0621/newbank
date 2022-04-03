package newbank.server;

import newbank.server.loan.AvailableLoan;
import newbank.server.loan.Loan;
import newbank.server.loan.LoanMarketplace;

import java.util.ArrayList;
import java.util.Collections;

public class Customer extends User{
	private ArrayList<Account> accounts;
	// for mirco-loan
	private ArrayList<AvailableLoan> availableLoans;
	private ArrayList<Loan> lentLoans;
	private ArrayList<Loan> borrowedLoans;
	private double remainingLoanLimit;

	public Customer(String userID, String password, String firstName, String lastName, String phone, String email, String address) {
		super(userID, password, firstName, lastName, phone, email, address);
		accounts = new ArrayList<>();
		availableLoans = new ArrayList<>();
		lentLoans = new ArrayList<>();
		borrowedLoans = new ArrayList<>();
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

	public String getLoanDetails() {
		String s = "";
		double totalAvailableLoan = getTotalAvailableLoans();
		double totalLentLoan = getTotalLentLoans();
		double totalBorrowedLoan = getTotalRemainingDebt();
		int accNum = accounts.size();
		if ((totalAvailableLoan + totalLentLoan) > 0) {
			s += "----------\n";
			s += (++accNum) + ") Lending account: " + (totalAvailableLoan + totalLentLoan) + ", where\n";
			s += String.format("%25s", "Lent with interest: ") + totalLentLoan + "\n";
			s += String.format("%25s", "Repaid/Not lent: ") + totalAvailableLoan + " (available to transfer to other accounts)\n";
		}
		if (totalBorrowedLoan > 0) {
			s += "----------\n";
			s += (++accNum) + ") Debt from other customers: " + totalBorrowedLoan + "\n";
		}
		return s;
	}

	public double getTotalAvailableLoans() {
		double total = 0;
		for (AvailableLoan aL : availableLoans) {
			total += aL.getAmount();
		}
		return total;
	}

	public double getTotalLentLoans() {
		double total = 0;
		for (Loan lL : lentLoans) {
			total += lL.getRemainingAmount();
		}
		return total;
	}

	public double getTotalRemainingDebt() {
		double total = 0;
		for (Loan bL: borrowedLoans) {
			total += bL.getRemainingAmount();
		}
		return total;
	}

	public double getRemainingLoanLimit() {
		return LoanMarketplace.getLoanLimit() - getTotalRemainingDebt() / (1 + LoanMarketplace.getInterestPerLoan());
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
		Collections.sort(availableLoans);
	}

	public void addLentLoan(Loan loan) {
		lentLoans.add(loan);
		Collections.sort(lentLoans);
	}

	public void addBorrowedLoan(Loan loan) {
		borrowedLoans.add(loan);
		Collections.sort(borrowedLoans);
	}

	public ArrayList<Loan> getBorrowedLoans() {
		return borrowedLoans;
	}

	public ArrayList<AvailableLoan> getAvailableLoans() {
		return availableLoans;
	}

}
