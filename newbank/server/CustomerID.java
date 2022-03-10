package newbank.server;

public class CustomerID {
	private String key;
	private String password;

	public CustomerID(String key, String password) {
		this.key = key;
		this.password = password;
	}
	
	public CustomerID(String key) {
		this(key, "");
	}
	
	public String getKey() {
		return key;
	}

	public boolean checkPassword(String password) {
		return this.password.equals(password);
	}
}
