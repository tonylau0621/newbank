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

	
	/** 
	 * @return String
	 */
	public String toString() {
		return (accountName + ": " + openingBalance);
	}

	
	/** 
	 * @param amount
	 */
	public void updateBalance(double amount){
		this.openingBalance += amount;
	}

	
	/** 
	 * @return String
	 */
	public String getID(){
		return this.id;
	}

	
	/** 
	 * @return double
	 */
	public double getAmount(){
		return this.openingBalance;
	}

	
	/** 
	 * @return String
	 */
	public String getAccount(){
		return this.accountName;
	}

	
	/** 
	 * @return String
	 */
	public String getUserID(){
		return this.id.split("-")[0];
	}



}
