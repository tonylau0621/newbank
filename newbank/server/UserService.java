package newbank.server;

import java.io.IOException;

public class UserService {
    public static CustomerID login() throws IOException {
        CommunicationService.sendOut("Enter Username");
        String userName = CommunicationService.readIn();
        // ask for password
        CommunicationService.sendOut("Enter Password");
        String password = CommunicationService.readIn();
        CommunicationService.sendOut("Checking Details...");
        // authenticate user and get customer ID token from bank for use in subsequent requests
        CustomerID customer = null;
        while(customer == null) {
            try {
                customer = NewBank.getBank().checkLogInDetails(userName, password);
            } catch (InvalidUserNameException | InvalidPasswordException iue) {
                CommunicationService.sendOut("Log In Failed");
                CommunicationService.sendOut(iue.getMessage());
            }
        }

        return customer;
    }
}
