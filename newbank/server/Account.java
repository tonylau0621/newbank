package newbank.server;

public class Account {
	
	private String accountName;
	private double openingBalance;

	public Account(String accountName, double openingBalance) {
		this.accountName = accountName;
		this.openingBalance = openingBalance;
	}
	
	public String toString() {
		return (accountName + ": " + openingBalance);
	}

	public void updateBalance(double amount){
		this.openingBalance += amount;
	}

	public double getAmount(){
		return this.openingBalance;
	}

	public String getAccount(){
		return this.accountName;
	}



}
