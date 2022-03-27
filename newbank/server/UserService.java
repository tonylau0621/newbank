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
        CommunicationService.sendOut("Enter Username");
        String userName = CommunicationService.readIn();
        CommunicationService.sendOut("Enter Password");
        String password = CommunicationService.readIn();
        CommunicationService.sendOut("Enter Firstname");
        String firstname = CommunicationService.readIn();
        CommunicationService.sendOut("Enter Lastname");
        String lastname = CommunicationService.readIn();
        CommunicationService.sendOut("Enter Phone");
        String phone = CommunicationService.readIn();
        CommunicationService.sendOut("Enter Email");
        String email = CommunicationService.readIn();
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

    public static Response loan(CustomerID customerID) throws IOException {
        Response response = new Response();
        Customer customer = NewBank.getBank().getCustomer(customerID);
        CommunicationService.sendOut("What do you want to do?\n 1) Put money to lending account\n 2) Take back money from the lending account" +
                "\n 3) Borrow money\n 4) Repay money");
        String functionRequest = CommunicationService.readIn();


        String request = "";
        String question1 = "";
        String question2 = "";

        switch (functionRequest) {
            case "1":
                request = "LEND";
                question1 = "You have chosen putting money to lending account.\nPlease choose the account you want to send money from:";
                question2 = "Please enter the amount you want to send:";
                break;
            case "2":
                request = "TAKEBACK";
                question1 = "You have chosen taking back lending money to other account.\nPlease choose the account you want to send money to:";
                question2 = "Please enter the amount you want to take:";
                break;
            case "3":
                request = "BORROW";
                question1 = "You have chosen borrowing money.\nPlease choose the account you want to send the borrowed money to:";
                question2 = "Please enter the amount you want to borrow:";
                break;
            case "4":
                request = "REPAY";
                question1 = "You have chosen repaying money.\nPlease choose the account you want to repay the borrowed money:";
                question2 = "Please enter the amount you want to repay:";
                break;
        }

        if (!request.equals("")) {
            ArrayList<Account> accounts = customer.getAccounts();
            CommunicationService.sendOut(question1);
            String accountName = CommunicationService.readIn();
            CommunicationService.sendOut(question2);
            String amount = CommunicationService.readIn();


            request = request + " " + accountName + " " + amount;
            try {
                return NewBank.getBank().processRequest(customerID, request);
            } catch (InvalidAmountException e) {
                e.printStackTrace();
            } catch (InsufficientBalanceException e) {
                e.printStackTrace();
            } catch (InvalidAccountException e) {
                e.printStackTrace();
            }
        }


        return null;
    }

}
