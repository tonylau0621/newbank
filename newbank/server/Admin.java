package newbank.server;

// class defining an Admin account
// This is almost identical to the customer class, minus the accounts field and possibly plus a field to set the admin clearance level
// Therefore in future, inheritance could be used to simplify this class and reduce redundancy 

public class Admin extends User {
  // private String privaleges; // add different levels of admin clearance, e.g. "admin", "superadmin", "owner"

  public Admin(String userID, String password, String firstName, String lastName, String phone, String email, String address) {
		super(userID, password, firstName, lastName, phone, email, address);
  }
}
