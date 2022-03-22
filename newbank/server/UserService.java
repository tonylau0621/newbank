package newbank.server;

import java.io.IOException;
import java.util.ArrayList;

public class UserService {
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
        } catch (InvalidUserNameException | InvalidPasswordException iue) {
            CommunicationService.sendOut("Log In Failed");
            Thread.sleep(500);
            CommunicationService.errorAndWait(iue);
        }
        return customer;
    }

    //show the accounts and return number of account
    public static ArrayList<Account> showAccounts(CustomerID customerID){
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
            }catch (NumberFormatException ne){
                throw new InvalidAccountException();
            }
            return NewBank.getBank().processRequest(customerID, request);
        } catch (InvalidAmountException | InsufficientBalanceException | InvalidAccountException e) {
            response.setCustomer(customerID);
            response.setResponseMessage(e.getMessage());
            return response;
        }
    }




}
