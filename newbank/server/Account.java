package newbank.server;

public class Account {
	
	private String id; //UserId + "-" + AccountId e.g. 000000001-1
	private String accountName;
	private double openingBalance;

	public Account(String id, String accountName, double openingBalance) {
		this.id = id;
		this.accountName = accountName;
		this.openingBalance = openingBalance;
	}

	public String toString() {
		return (accountName + ": " + openingBalance);
	}

	public void updateBalance(double amount){
		this.openingBalance += amount;
	}

	public String getID(){
		return this.id;
	}

	public double getAmount(){
		return this.openingBalance;
	}

	public String getAccount(){
		return this.accountName;
	}

	public String getUserID(){
		return this.id.split("-")[0];
	}



}
