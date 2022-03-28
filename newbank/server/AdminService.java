package newbank.server;

import java.io.IOException;

public class AdminService {
  public static CustomerID login() throws IOException, InterruptedException {
    CommunicationService.sendOut("Enter Username");
    String userName = CommunicationService.readIn();
    // ask for password
    CommunicationService.sendOut("Enter Password");
    String password = CommunicationService.readIn();
    CommunicationService.sendOut("Checking Details...");
    Thread.sleep(1000);
    // authenticate user and get customer ID token from bank for use in subsequent requests
    CustomerID customer = null;
    try {
        customer = NewBank.getBank().adminLogin(userName, password);
    } catch (InvalidUserNameException | InvalidPasswordException iue) {
        CommunicationService.sendOut("Log In Failed");
        Thread.sleep(500);
        CommunicationService.errorAndWait(iue);
    }
    return customer;
  }
}
