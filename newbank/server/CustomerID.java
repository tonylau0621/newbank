package newbank.server;

public class CustomerID {
	private String key; //as username
	private String password;
	private String firstName;
	private String lastName;
	private String phone;
	private String email;
	private String address;

	public CustomerID(String key, String password, String firstName, String lastName, String phone, String email, String address) {
		this.key = key;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.email = email;
		this.address = address;
	}

	public CustomerID(String key, String password) {
		this(key, password, "", "", "", "", "");
	}
	
	public CustomerID(String key) {
		this(key, "", "", "", "", "", "");
	}
	
	public String getKey() {
		return key;
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

}
