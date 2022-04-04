package newbank.server;

public class User {
  private String userID;
	private String password;
	private String firstName;
	private String lastName;
	private String phone;
	private String email;
	private String address;

  public User(String userID, String password, String firstName, String lastName, String phone, String email, String address) {
		this.userID = userID;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phone = phone;
		this.email = email;
		this.address = address;
  }

  
	/** 
	 * @param password
	 * @return boolean
	 */
	public boolean checkPassword(String password) {
		return this.password.equals(password);
	}

	
	/** 
	 * @param oldPassword
	 * @param newPassword
	 * @return boolean
	 */
	// oldPassword should be correct to change password
	public boolean setPassword(String oldPassword, String newPassword) {
		if (checkPassword(oldPassword)) {
			this.password = newPassword;
			return true;
		}
		return false;
	}

	
	/** 
	 * @return String
	 */
	// other getters and setters
	// no getPassword to avoid password leakage
	public String getFirstName() {
		return firstName;
	}

	
	/** 
	 * @param firstName
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	
	/** 
	 * @return String
	 */
	public String getLastName() {
		return lastName;
	}

	
	/** 
	 * @param lastName
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	
	/** 
	 * @return String
	 */
	public String getEmail() {
		return email;
	}

	
	/** 
	 * @param email
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	
	/** 
	 * @return String
	 */
	public String getPhone() {
		return phone;
	}

	
	/** 
	 * @param phone
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}

	
	/** 
	 * @return String
	 */
	public String getAddress() {
		return address;
	}

	
	/** 
	 * @param address
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	
	/** 
	 * @return String
	 */
	public String getUserID() {
		return userID;
	}

	public String getPassword() { return password; }
}
