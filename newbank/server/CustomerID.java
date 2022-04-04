package newbank.server;

/**
 * Unique identifier for a customer, used to indentify a single login session with the bank server.
 * This is used to determine which requests come from which customers, and whether the customer has admin privaleges.
 */
public class CustomerID {
	private String key; //as username
	private boolean isAdmin;


	public CustomerID(String key, boolean isAdmin) {
		this.key = key;
		this.isAdmin = isAdmin;
	}

	
	/** 
	 * @return String
	 */
	public String getKey() {
		return key;
	}

	
	/** 
	 * @return boolean
	 */
	public boolean isAdmin() {
		return isAdmin;
	}

}
