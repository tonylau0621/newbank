package newbank.server;

/**
 * Class defining an Admin account and containing admin-specific methods.
 * @see User
 */
public class Admin extends User {
  // private String privaleges; // add different levels of admin clearance, e.g. "admin", "superadmin", "owner"

  public Admin(String userID, String password, String firstName, String lastName, String phone, String email, String address) {
		super(userID, password, firstName, lastName, phone, email, address);
  }
}
