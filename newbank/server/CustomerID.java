package newbank.server;

public class CustomerID {
	private String key; //as username
	private boolean isAdmin;


	public CustomerID(String key, boolean isAdmin) {
		this.key = key;
		this.isAdmin = isAdmin;
	}

	public String getKey() {
		return key;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

}
