package newbank.server;

import newbank.form_service.Email;
import newbank.form_service.Password;
import newbank.form_service.Phone;
import newbank.form_service.UserName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class UserService {
    public static final Integer MAX_LOGIN_ATTEMPT = 3;
    public static Map<String, Integer> userMapByLoginAttempt = new HashMap<>();
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
            customer = NewBank.getBank().checkLogInDetails(userName, password);
        } catch (InvalidUserNameException | InvalidPasswordException | MaxLoginAttemptReachException iue) {
            CommunicationService.sendOut("Log In Failed");
            Thread.sleep(500);
            CommunicationService.errorAndWait(iue);
        }
        return customer;
    }

    //show the accounts for the user to choose.
    private static ArrayList<Account> showAccounts(CustomerID customerID){
        ArrayList<Account> accounts = NewBank.getBank().getCustomer(customerID).getAccounts();
        for (int i=0; i< accounts.size(); i++){
            CommunicationService.sendOut(String.valueOf(i+1)+") "+ accounts.get(i).getAccount() + ": " + accounts.get(i).getAmount());
        }
        return accounts;
    }

    public static Response move(CustomerID customerID) throws IOException{
        Response response = new Response();
        ArrayList<Account> accounts = showAccounts(customerID);
        CommunicationService.sendOut("Please choose the account you want to send money from");
        String from = CommunicationService.readIn();
        CommunicationService.sendOut("Please choose the account you want to send money to");
        String to = CommunicationService.readIn();
        if (from.equals(to)){
            response.setCustomer(customerID);
            response.setResponseMessage("You cannot choose the same account, please try again");
            return response;
        }
        CommunicationService.sendOut("Please enter the amount you want to send:");
        String amount = CommunicationService.readIn();
        String request;
        try {
            try{
                request = "MOVE" + " " + amount + " " + accounts.get(Integer.parseInt(from)-1).getAccount()  + " " + accounts.get(Integer.parseInt(to)-1).getAccount(); 
            }catch (NumberFormatException | IndexOutOfBoundsException ne){
                throw new InvalidAccountException();
            }
            return NewBank.getBank().processRequest(customerID, request);
        } catch (InvalidAmountException | InsufficientBalanceException | InvalidAccountException e) {
            response.setCustomer(customerID);
            response.setResponseMessage(e.getMessage());
            return response;
        }
    }

    //Add new customer
    public static Response newCustomer() throws IOException, InterruptedException{
        String userName = new UserName().getInput();
        String password = new Password().getInput();
        CommunicationService.sendOut("Enter Firstname");
        String firstname = CommunicationService.readIn();
        CommunicationService.sendOut("Enter Lastname");
        String lastname = CommunicationService.readIn();
        String phone = new Phone().getInput();
        String email = new Email().getInput();
        CommunicationService.sendOut("Enter Address");
        String address = CommunicationService.readIn();
        //Send to Newbank
        try {
            return NewBank.getBank().addCustomer(userName, password, firstname, lastname, phone, email, address);
        } catch (InvalidUserNameException e) {
            CommunicationService.sendOut("Log In Failed");
            Thread.sleep(500);
            CommunicationService.errorAndWait(e);
            return null;
        }

    }
}
