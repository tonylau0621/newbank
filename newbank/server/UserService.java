package newbank.server;

import newbank.server.loan.LoanMarketplace;
import newbank.form_service.Email;
import newbank.form_service.Password;
import newbank.form_service.Phone;
import newbank.form_service.UserName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.util.ElementScanner6;

/**
 * Contains the functionality of all the user operations within the bank.
 * See the individual methods for more details.
 */
public class UserService {
    public static final Integer MAX_LOGIN_ATTEMPT = 3;
    public static Map<String, Integer> userMapByLoginAttempt = new HashMap<>();
    
    /** 
     * Asks for the client's login details, and attempts to log in with a new CustomerID.
     * 
     * @return CustomerID
     * @throws IOException
     * @throws InterruptedException
     */
    public static CustomerID login() throws IOException, InterruptedException, SessionTimeoutException {
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

    
    /** 
     * Shows the accounts held by a customer.
     * 
     * @param customerID
     * @return ArrayList<Account>
     */
    //show the accounts for the user to choose.
    private static ArrayList<Account> showAccounts(CustomerID customerID){
        ArrayList<Account> accounts = NewBank.getBank().getCustomer(customerID).getAccounts();
        for (int i=0; i< accounts.size(); i++){
            CommunicationService.sendOut(String.valueOf(i+1)+") "+ accounts.get(i).getID() + " " + accounts.get(i).getAccount() + ": " + accounts.get(i).getAmount());
        }
        return accounts;
    }

    
    /** 
     * Moves money between two accounts held by one customer.
     * 
     * @param customerID
     * @return Response
     * @throws IOException
     */
    public static Response move(CustomerID customerID) throws IOException, SessionTimeoutException {
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
        } catch (InvalidAmountException | InsufficientBalanceException | InvalidAccountException | InvalidUserNameException e) {
            response.setCustomer(customerID);
            response.setResponseMessage(e.getMessage());
            return response;
        }
    }

    
    /** 
     * Pays money from the current customer's account, to a chosen account held by another customer.
     * 
     * @param customerID
     * @return Response
     * @throws IOException
     */
    public static Response pay(CustomerID customerID) throws IOException, SessionTimeoutException {
        Response response = new Response();
        ArrayList<Account> accounts = showAccounts(customerID);
        CommunicationService.sendOut("Please choose the account you want to use for payment");
        String payingAccount = CommunicationService.readIn();
        CommunicationService.sendOut("Please input the account number you want to send money to");
        String receivingCustomerKey = CommunicationService.readIn();
        CommunicationService.sendOut("Please enter the amount you want to send:");
        String amount = CommunicationService.readIn();
        String request;
        try {
            try{
                String[] temp = receivingCustomerKey.split("-");
                if (temp.length > 2) throw new IndexOutOfBoundsException();
                receivingCustomerKey = temp[0];
                String receivingAccount = temp[0]+"-"+temp[1];
                request = "PAY" + " " + amount + " " + accounts.get(Integer.parseInt(payingAccount)-1).getAccount()  + " " + receivingCustomerKey + " " + receivingAccount;
            }catch (NumberFormatException | IndexOutOfBoundsException ne){
                throw new InvalidAccountException();
            }
            return NewBank.getBank().processRequest(customerID, request);
        } catch (InvalidAmountException | InsufficientBalanceException | InvalidAccountException | InvalidUserNameException e) {
            response.setCustomer(customerID);
            response.setResponseMessage(e.getMessage());
            return response;
        }
    }

    
    /** 
     * Creates a new empty account for the customer, with a custom name.
     * 
     * @param customerID
     * @return Response
     * @throws IOException
     */
    public static Response newAccount(CustomerID customerID) throws IOException, SessionTimeoutException {
        Response response = new Response();
        CommunicationService.sendOut("Enter Account Name");
        String accName = CommunicationService.readIn();
        try{
            if ((!accName.equals("")) && (accName.matches("^[a-zA-Z]*$"))){
                String request = "NEWACCOUNT " +accName;
                return NewBank.getBank().processRequest(customerID, request);
            }else{
                throw new InvalidAccountException();
            }
        }catch (InvalidAmountException | InsufficientBalanceException | InvalidUserNameException e) {
            response.setCustomer(customerID);
            response.setResponseMessage(e.getMessage());
            return response;
        }catch (InvalidAccountException iae){
            response.setCustomer(customerID);
            response.setResponseMessage("Illegal Account Name");
            return response;
        }
    }

    
    /** 
     * Unlocks a user that has been locked out of the system for too many failed login attempts.
     * 
     * @return String
     * @throws IOException
     * @throws InvalidUserNameException
     */
    public static String unlockUser() throws IOException, InvalidUserNameException, SessionTimeoutException {
        CommunicationService.sendOut("Enter username to unlock");
        String username = CommunicationService.readIn();
        boolean isValidUserName = (NewBank.getBank().getCustomers().containsKey(username));
        if(!isValidUserName) throw new InvalidUserNameException();
        UserService.userMapByLoginAttempt.put(username, 0);
        return username + " has been unlocked.";
    }

    
    /** 
     * Adds a new customer to the system.
     * Creates a unique customer ID and adds the customer to the bank.
     * 
     * @return Response
     * @throws IOException
     * @throws InterruptedException
     */
    //Add new customer
    public static Response newCustomer() throws IOException, InterruptedException, SessionTimeoutException {
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
            CommunicationService.sendOut("Registration Failed");
            Thread.sleep(500);
            CommunicationService.errorAndWait(e);
            return null;
        }

    }

    public static Response loan(CustomerID customerID) throws IOException, SessionTimeoutException {
        Response response = new Response();
        Customer customer = NewBank.getBank().getCustomer(customerID);

        CommunicationService.sendOut("What do you want to do?\n 1) Put money to lending account\n 2) Take back money from the lending account" +
                "\n 3) Borrow money\n 4) Repay money");
        String functionRequest = CommunicationService.readIn();


        String request = "";
        String question1 = "";
        String question2 = "";
        String question3 = "";

        switch (functionRequest) {
            case "1":
                request = "LEND";
                question1 = "You have chosen putting money to lending account.\nPlease choose the account you want to send money from:";
                question2 = "Please enter the amount you want to send:";
                break;
            case "2":
                if (customer.getTotalAvailableLoans() > 0) {
                    request = "TAKEBACK";
                    question1 = "You have chosen taking back lending money to other account.\nPlease choose the account you want to send money to:";
                    question2 = "Please enter the amount you want to take:";
                } else {
                    CommunicationService.sendOut("You don't have any available amount can be transferred.");
                }
                break;
            case "3":
                request = "BORROW";
                question1 = "You have chosen borrowing money.\n";
                question1 += "Total available loan amount in the market: " + NewBank.getBank().getLoanMarketplace().getTotalAvailableLoanAmount(customer.getUserID()) + "\n";
                question1 += "Amount you have borrowed: " + (customer.getTotalRemainingDebt() / (1 + LoanMarketplace.getInterestPerLoan())) + "\n";
                question1 += "Amount you can borrow: " + Math.min(customer.getRemainingLoanLimit(), NewBank.getBank().getLoanMarketplace().getTotalAvailableLoanAmount(customer.getUserID())) + "\n";
                question1 += "Please choose the account you want to send the borrowed money to:";
                question2 = "Please enter the amount you want to borrow:";
                break;
            case "4":
                if (customer.getTotalRemainingDebt() > 0) {
                    request = "REPAY";
                    question1 = "You have chosen repaying money.\nPlease choose the account you want to repay the borrowed money:";
                    question2 = "Please enter the amount you want to repay:";
                } else {
                    CommunicationService.sendOut("You don't have any debt.");
                }
                break;
        }

        if (!request.equals("")) {
            CommunicationService.sendOut("Your accounts details:");
            CommunicationService.sendOut(NewBank.getBank().showMyAccounts(customerID));
            CommunicationService.sendOut(question1);
            String accountName = CommunicationService.readIn();
            CommunicationService.sendOut(question2);
            String amount = CommunicationService.readIn();


            request = request + " " + accountName + " " + amount;
            try {
                if (accountName.contains(" ")) throw new InvalidAccountException();

                // Confirmation
                try {
                    double amountDouble = Double.parseDouble(amount);
                    switch (functionRequest) {
                        case "1":
                            if (amountDouble <= customer.getAccount(accountName).getAmount()) {
                                question3 = "Are you sure to send " + amountDouble + " from " + accountName + " to lending account? (Y/N)";
                            } else {
                                throw new InsufficientBalanceException();
                            }
                            break;
                        case "2":
                            if (amountDouble <= customer.getTotalAvailableLoans()) {
                                question3 = "Are you sure to send " + amountDouble + " from lending account to " + accountName + "? (Y/N)";
                            } else {
                                throw new InsufficientBalanceException();
                            }
                            break;
                        case "3":
                            if (amountDouble <= Math.min(customer.getRemainingLoanLimit(), NewBank.getBank().getLoanMarketplace().getTotalAvailableLoanAmount(customer.getUserID()))) {
                                question3 = "Are you sure to borrow " + amountDouble + " and send it to " + accountName + "? (Y/N)";
                            } else {
                                throw new InvalidAmountException();
                            }
                            break;
                        case "4":
                            if (amountDouble <= customer.getTotalRemainingDebt() && amountDouble <= customer.getAccount(accountName).getAmount()) {
                                question3 = "Are you sure to repaid " + amountDouble + " from " + accountName + "? (Y/N)";
                            } else if (amountDouble > customer.getTotalRemainingDebt() && amountDouble <= customer.getAccount(accountName).getAmount()) {
                                question3 = "You have entered the repaid amount " + amountDouble + " which is more than enough. You only need to repay " + customer.getTotalRemainingDebt();
                                question3 += "\nAre you sure to repay " + customer.getTotalRemainingDebt() + " from " + accountName + "? (Y/N)";
                            } else {
                                throw new InsufficientBalanceException();
                            }
                            break;
                    }
                } catch (NumberFormatException e) {
                    throw new InvalidAmountException();
                }

                CommunicationService.sendOut(question3);
                String confirmation = CommunicationService.readIn();

                if (confirmation.trim().equalsIgnoreCase("Y")) {
                    return NewBank.getBank().processRequest(customerID, request);
                }

            } catch (InvalidAmountException | InsufficientBalanceException | InvalidAccountException | InvalidUserNameException e) {
                response.setCustomer(customerID);
                response.setResponseMessage(e.getMessage());
                return response;
            }
        }
        response.setCustomer(customerID);
        response.setResponseMessage("");
        return response;
    }

    public static Response transaction(CustomerID customerID) throws IOException {
        Response response = new Response();
        CommunicationService.sendOut("1) Transaction Record for all account\n2) Transaction Record for specific account");
        String option = CommunicationService.readIn();
        try{

            if (option.equals("1")) {
                return NewBank.getBank().processRequest(customerID, "TRANSACTIONRECORD");
            }
            else if (option.equals("2")) {
                ArrayList<Account> accounts = showAccounts(customerID);
                CommunicationService.sendOut("Please choose the account you want to check");
                String account = CommunicationService.readIn();
                try {
                    account = accounts.get(Integer.parseInt(account)-1).getID();
                    return NewBank.getBank().processRequest(customerID, "TRANSATIONRECORDACC "+account);
                }catch (NumberFormatException | IndexOutOfBoundsException ne){
                    throw new InvalidAccountException();
                }
            }
            else{
                response.setCustomer(customerID);
                response.setResponseMessage("Invalid Input");
                return response;
            }
        } catch (InvalidAmountException | InsufficientBalanceException | InvalidAccountException | InvalidUserNameException e) {
            response.setCustomer(customerID);
            response.setResponseMessage(e.getMessage());
            return response;
        }


    }

}
