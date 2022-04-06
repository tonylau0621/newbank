package newbank.server;

// Only user for testing
//import java.util.ArrayList;

import newbank.server.loan.AvailableLoan;
import newbank.server.loan.Loan;
import newbank.server.loan.LoanMarketplace;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

// Please comment out all "Only use for testing" method/block/statement for real use.
public class NewBank {

	private static final NewBank bank = new NewBank();
  private static LoanMarketplace loanMarketplace;
	private HashMap<String,Customer> customers;
	private ArrayList<Transaction> transactions;
	private HashMap<String,Admin> admins;

  // Only use for testing
  //public ArrayList<CustomerID> customersID;

	private NewBank() {
		customers = DataHandler.readCustData();
		transactions = DataHandler.readTransaction();
    admins = new HashMap<>();
    loanMarketplace = new LoanMarketplace();
    addTestData();
	}

	private void addTestData() {
		Admin admin = new Admin("32462385", "adminPass", "", "", "", "", "");
		admins.put("Admin", admin);
	}

  public void addLoanData() {
    // Loan
    ArrayList<AvailableLoan> availableLoansFromDatabase = DataHandler.readAvailableLoan();
    ArrayList<Loan> loansFromDatabase = DataHandler.readLoan();
    long maxAvailableLoanID = 0;
    long maxLoanID = 0;
    for (AvailableLoan availableLoan : availableLoansFromDatabase) {
      loanMarketplace.addAvailableLoan(availableLoan);
      maxAvailableLoanID = Math.max(maxAvailableLoanID, availableLoan.getAvailableLoanID());
    }
    AvailableLoan.setMaxAvailableLoanID(maxAvailableLoanID);
    for (Loan loan : loansFromDatabase) {
      loanMarketplace.addLoan(loan);
      maxLoanID = Math.max(maxLoanID, loan.getLoanID());
    }
    Loan.setMaxLoanID(maxLoanID);
  }

  public static NewBank getBank() {
    return bank;
  }

  public static LoanMarketplace getLoanMarketplace() {
    return loanMarketplace;
  }

  public synchronized CustomerID checkLogInDetails(String userName, String password) throws InvalidUserNameException, InvalidPasswordException, MaxLoginAttemptReachException {
    boolean isValidUserName = (customers.containsKey(userName));
    if(!isValidUserName) throw new InvalidUserNameException();
    if(UserService.userMapByLoginAttempt.containsKey(userName)) {
      Integer userLoginAttempt = UserService.userMapByLoginAttempt.get(userName);
      if(userLoginAttempt >= UserService.MAX_LOGIN_ATTEMPT) throw new MaxLoginAttemptReachException();
    }
    Customer targetCustomer = customers.get(userName);
    CustomerID targetCustomerId = new CustomerID(userName, false);
    boolean isValidPassword = (targetCustomer.checkPassword(password));
    if(!isValidPassword) {
      if(!UserService.userMapByLoginAttempt.containsKey(userName)) {
        UserService.userMapByLoginAttempt.put(userName, 1);
      } else {
        Integer userLoginAttempt = UserService.userMapByLoginAttempt.get(userName);
        if(userLoginAttempt < UserService.MAX_LOGIN_ATTEMPT) {
          userLoginAttempt++;
          UserService.userMapByLoginAttempt.put(userName, userLoginAttempt);
        }
      }
      throw new InvalidPasswordException();
    }
    return targetCustomerId;
  }

  public synchronized CustomerID adminLogin(String userName, String password) throws InvalidUserNameException, InvalidPasswordException {
    boolean isValidUserName = (admins.containsKey(userName));
    if(!isValidUserName) throw new InvalidUserNameException();
    Admin targetCustomer = admins.get(userName);
    CustomerID targetCustomerId = new CustomerID(userName, true);
    boolean isValidPassword = (targetCustomer.checkPassword(password));
    if(!isValidPassword) throw new InvalidPasswordException();
    return targetCustomerId;
  }


  // commands from the NewBank customer are processed in this method
  public synchronized Response processRequest(CustomerID customer, String request) throws IOException, InvalidAmountException, InsufficientBalanceException, InvalidAccountException, InvalidUserNameException {
    String[] requestTokens = request.split("\\s+");
    String requestFunction = requestTokens[0];
    Response response = new Response();
    response.setCustomer(customer);
    switch(requestFunction) {
      case "SHOWMYACCOUNTS" : {
        response.setResponseMessage(showMyAccounts(customer));
        return response;
      }
      case "NEWACCOUNT" :
        if (requestTokens.length > 1) {
          response.setResponseMessage(newAccount(customer, requestTokens[1]));
          return response;
        }
        return null;
      case "MOVE" :
        if (requestTokens.length > 3) {
          response.setResponseMessage(moveAmount(requestTokens[1], requestTokens[2], requestTokens[3], customer));
          return response;
        }
        return null;
      case "PAY" :
        if (requestTokens.length > 4) {
          response.setResponseMessage(payAmount(Double.parseDouble(requestTokens[1]), customer, requestTokens[2], requestTokens[3], requestTokens[4]));
          return response;
        }
        return null;
      case "LOGOUT" :
        response.setCustomer(null);
        response.setResponseMessage("Logout Successful.");
        return response;
      case "TRANSACTIONRECORD":
        response.setResponseMessage(getTransactionRecord(customer));
        return response;
      case "UNLOCKUSER":
        response.setResponseMessage(UserService.unlockUser());
        return response;
      case "LEND":
        if (requestTokens.length > 2) {
          response.setResponseMessage(offerLoan(customer, requestTokens[1], Double.parseDouble(requestTokens[2])));
          return response;
        }
        break;
      case "TAKEBACK":
        if (requestTokens.length > 2) {
          response.setResponseMessage(takeBack(customer, requestTokens[1], Double.parseDouble(requestTokens[2])));
          return response;
        }
        break;
      case "BORROW":
        if (requestTokens.length > 2) {
          response.setResponseMessage(borrow(customer, requestTokens[1], Double.parseDouble(requestTokens[2])));
          return response;
        }
        break;
      case "REPAY":
        if (requestTokens.length > 2) {
          response.setResponseMessage(repay(customer, requestTokens[1], Double.parseDouble(requestTokens[2])));
          return response;
        }
        break;
      default : {
        response.setResponseMessage("Invalid Input");
        return response;
      }
    }
    response.setResponseMessage("Invalid Input");
    return response;
  }

  public String showMyAccounts(CustomerID customer) {
    ArrayList<Account> accounts = this.getCustomer(customer).getAccounts();
    String result = "";
    for (int i = 0; i < accounts.size(); i++) {
      result += String.valueOf(i + 1) + ") " + accounts.get(i).getAccount() + ": " + accounts.get(i).getAmount() + "\n";
    }
    // To show mirco-loan details
    result += this.getCustomer(customer).getLoanDetails();
    return result;
  }

  private String newAccount(CustomerID customer, String accountName) throws IOException {
    ArrayList<Account> accounts = customers.get(customer.getKey()).getAccounts();
    for (Account account : accounts){
      if (account.getAccount().equals(accountName)){
        return "Account Name already exists! Please try again";
      }
    }
    String id = customers.get(customer.getKey()).getUserID() + "-" + (accounts.size()+1);
    Account result = new Account(id, accountName, 0.0);
    customers.get(customer.getKey()).addAccount(result);
    DataHandler.updateAccountCSV(customers);
    return "Account "+result.getAccount()+" is created, the account number is:" + result.getID();

  }

  private String moveAmount(String value, String from, String to, CustomerID customer) throws InvalidAmountException, InsufficientBalanceException, InvalidAccountException, IOException{
    double amount;
    try{
      amount = Double.parseDouble(value);
    }catch(NumberFormatException e){
      throw new InvalidAmountException();
    }
    //Negative amount
    if (amount < 0.01){
      throw new InvalidAmountException();
    }

    //Account does not exist
    Customer target = customers.get(customer.getKey());
    Account accFrom = target.getAccount(from);
    Account accTo = target.getAccount(to);
    if (accFrom == null || accTo == null){
      throw new InvalidAccountException();
    }

    //Not enough balance
    if (accFrom.getAmount() < amount){
      throw new InsufficientBalanceException();
    }

    //Update the amount and return success
    accFrom.updateBalance(-amount);
    accTo.updateBalance(amount);
    DataHandler.updateAccountCSV(customers);
    addTransaction(new Transaction(accFrom.getID(), accTo.getID(), amount, "Transfer"));
    return value +  " has been moved from " + from + " to " + to;
  }

  // PAY command
  //Do I want to use Accounts or Customers?
  // payAmount: Takes two accounts and an amount as input
  private String payAmount(double amount, CustomerID payingCustomer, String payingAccount,
                           String receivingCustomerKey, String receivingAccount) {
    //Account does not exist
    Customer payer = customers.get(payingCustomer.getKey());
    Customer receiver = customers.get(receivingCustomerKey);

    if (payer.getAccount(payingAccount) == null || receiver.getAccount(receivingAccount) == null) {
      return "Please check Accounts";


    }
    //Customers should not be able to transfer less than 0.01
    if(amount < 0.01) {return "Invalid amount";}

	// commands from the NewBank customer are processed in this method
	public synchronized Response processRequest(CustomerID customer, String request) throws IOException, InvalidAmountException, InsufficientBalanceException, InvalidAccountException, InvalidUserNameException {
		String[] requestTokens = request.split("\\s+");
		String requestFunction = requestTokens[0];
		Response response = new Response();
		response.setCustomer(customer);
		switch(requestFunction) {
			case "SHOWMYACCOUNTS" : {
				response.setResponseMessage(showMyAccounts(customer));
				return response;
			}
			case "NEWACCOUNT" :
				if (requestTokens.length > 1) {
					response.setResponseMessage(newAccount(customer, requestTokens[1]));
					return response;
				}
				return null;
			case "MOVE" :
				if (requestTokens.length > 3) {
					response.setResponseMessage(moveAmount(requestTokens[1], requestTokens[2], requestTokens[3], customer));
					return response;
				}
				return null;
			case "PAY" :
				if (requestTokens.length > 4) {
					response.setResponseMessage(payAmount(Double.parseDouble(requestTokens[1]), customer, requestTokens[2], requestTokens[3], requestTokens[4]));
					return response;
				}
				return null;
			case "LOGOUT" :
				response.setCustomer(null);
				response.setResponseMessage("Logout Successful.");
				return response;
			case "TRANSACTIONRECORD":
				response.setResponseMessage(getTransactionRecord(customer));
				return response;
			case "UNLOCKUSER":
				response.setResponseMessage(UserService.unlockUser());
				return response;
			default : {
				response.setResponseMessage("Invalid Input");
				return response;
			}
		}
	}

	private String showMyAccounts(CustomerID customer) {
		ArrayList<Account> accounts = this.getCustomer(customer).getAccounts();
		String result = "";
        for (int i=0; i< accounts.size(); i++){
            result += accounts.get(i).getID() + " " + accounts.get(i).getAccount() + ": " + accounts.get(i).getAmount() + "\n";
        }
		return result;
	}


    //get Account Balance Amount
    double balance = payer.getAccount(payingAccount).getAmount();
    //Is there enough balance?
    if(balance >= amount) {
      //Adjust balance for both accounts
      payer.getAccount(payingAccount).updateBalance(-amount);
      receiver.getAccount(receivingAccount).updateBalance(amount);

      //updated_balance = payer.getAccount(payingAccount).getAmount();

      //return "Payment was successful.";
      return "Payment successful";
    }
    else {
      return "Insufficient balance.";
    }


  }

	// PAY command
	//Do I want to use Accounts or Customers?
	// payAmount: Takes two accounts and an amount as input
	private String payAmount(double amount, CustomerID payingCustomer, String payingAccount,
							 String receivingCustomerKey, String receivingAccount) throws IOException {
		//Account does not exist
		Customer payer = customers.get(payingCustomer.getKey());
		Customer receiver = null;
		for (HashMap.Entry<String, Customer> entry : customers.entrySet()){
			if (entry.getValue().getUserID().equals(receivingCustomerKey)){
				receiver = customers.get(entry.getKey());
			}
		}
	
		if (receiver == null || payer.getAccount(payingAccount) == null || receiver.getAccountbyID(receivingAccount) == null) {
			return "Please check Accounts";

		}
		//Customers should not be able to transfer less than 0.01
		if(amount < 0.01) {return "Invalid amount";}

		//get Account Balance Amount
		double balance = payer.getAccount(payingAccount).getAmount();
		//Is there enough balance?
		if(balance >= amount) {
			//Adjust balance for both accounts
			payer.getAccount(payingAccount).updateBalance(-amount);
			receiver.getAccountbyID(receivingAccount).updateBalance(amount);
			DataHandler.updateAccountCSV(customers);
			addTransaction(new Transaction(payer.getAccount(payingAccount).getID(), receivingAccount, amount, "Payment "));
			//updated_balance = payer.getAccount(payingAccount).getAmount();

			//return "Payment was successful.";
			return "Payment successful";
		}
		else {
			return "Insufficient balance.";
		}


  public Customer getCustomer(CustomerID id){
    return customers.get(id.getKey());
  }

  public Customer getCustomer(String userID) {
    Customer[] customerArr = customers.values().toArray(new Customer[0]);
    for (int i = 0; i < customerArr.length; i++) {
      if (customerArr[i].getUserID().equals(userID)) {
        return customerArr[i];
      }
    }
    return null;
  }

  public HashMap<String,Customer> getCustomers() { return customers; }

  public Response addCustomer(String username, String password, String firstName, String lastName, String phone, String email, String address) throws InvalidUserNameException, IOException {
    if (customers.keySet().contains(username)) {
      throw new InvalidUserNameException("Username already exists!");
    }
    String userID = generateUserID();
    Customer customer = new Customer(userID, password, firstName, lastName, phone, email, address);
    customer.addAccount(new Account(userID + "-1", "Main", 0.0));
    customers.put(username, customer);
    Response response = new Response();
    response.setResponseMessage("Registration completed. Please Login with your username and password");
    DataHandler.updateCustomerCSV(customers);
    DataHandler.updateAccountCSV(customers);
    return response;
  }

  // If all 99999999 userIDs are occupied, this method will perform infinite loop.
  // Can be changed to private for real use.
  public String generateUserID() {
    Random ran = new Random();
    generateAgain:
    while (true) {
      String userID = String.valueOf(ran.nextInt(99999999) + 1);
      for (int i = userID.length(); i < 8; i++) {
        userID = "0" + userID;
      }

      Customer[] customerArr = customers.values().toArray(new Customer[0]);
      for (int i = 0; i < customerArr.length; i++) {
        if (customerArr[i].getUserID().equals(userID)) {
          continue generateAgain;
        }
      }
      return userID;
    }
  }

  private String getTransactionRecord(CustomerID customer){
    ArrayList<Transaction> transactionRecord = getLast10Transactions(customer);
    String result;
    DateTimeFormatter datetime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());
    if (transactionRecord.size() == 0){
      return "No record found";
    }else{
      result = "Date       Time  Type     From       To         Amount\n";
      for (Transaction t : transactionRecord){
        result += datetime.format(t.getDT()) +" "+t.getType()+" "+t.getFrom()+" "+t.getTo()+" "+ String.valueOf(t.getAmount())+ "\n";
      }
    }
    return result;
  }

  private ArrayList<Transaction> getLast10Transactions(CustomerID customer){
    //get UserID
    String userID = customers.get(customer.getKey()).getUserID();
    ArrayList<Transaction> result = new ArrayList<>();
    for (int i = 0; i < transactions.size(); i++){
      if (transactions.get(i).getFrom().split("-")[0].equals(userID) || transactions.get(i).getTo().split("-")[0].equals(userID)){
        result.add(transactions.get(i));
      }
      if (result.size() == 10) break;
    }
    return result;
  }

  public void addTransaction(Transaction transaction) throws IOException{
    transactions.add(0, transaction);
    DataHandler.updateTransactionCSV(this.transactions);
  }

  // for micro-loan
  private String offerLoan(CustomerID customer, String accountName, double amount) throws InvalidAmountException, InsufficientBalanceException, InvalidAccountException, IOException {
    Customer lender = customers.get(customer.getKey());
    if (loanMarketplace.offerLoan(lender.getUserID(), accountName, amount)) {
      return "You have moved " + amount + " to the lending account.";
    }
    return "Failed to move " + amount + " to the lending account.";
  }


  private String takeBack(CustomerID customer, String accountName, double amount) throws InvalidAmountException, InsufficientBalanceException, InvalidAccountException, IOException {
    Customer lender = customers.get(customer.getKey());
    if (loanMarketplace.transferLendingAccountToOtherAccount(lender.getUserID(), accountName, amount)) {
      return "You have moved " + amount + " from the lending account to " + accountName;
    }
    return "Failed to move " + amount + " from the lending account to " + accountName;
  }

	private String getTransactionRecord(CustomerID customer){
		ArrayList<Transaction> transactionRecord = getLast10Transactions(customer);
		String result;
		DateTimeFormatter datetime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());
		if (transactionRecord.size() == 0){
			return "No record found";
		}else{
			result = "Date       Time  Type     From       To         Amount\n";
			for (Transaction t : transactionRecord){
				String type = t.getType();
				String cusID = customers.get(customer.getKey()).getUserID();
				if (type.equals("Payment ") && t.getTo().split("-")[0].equals(cusID)){
					type = "Received";
				}
				result += datetime.format(t.getDT()) +" "+type+" "+t.getFrom()+" "+t.getTo()+" "+ String.valueOf(t.getAmount())+ "\n";
			}
		}
		return result;
	}


  private String borrow(CustomerID customer, String accountName, double amount) throws InvalidAmountException, InvalidAccountException, IOException {
    Customer borrower = customers.get(customer.getKey());

    double borrowLimit = Math.min(borrower.getRemainingLoanLimit(), loanMarketplace.getTotalAvailableLoanAmount());
    amount = amount <= borrowLimit ? amount : borrowLimit;
    if (loanMarketplace.processLoanRequest(borrower.getUserID(), accountName, amount)) {
      return "You have borrowed " + amount + " and it has been sent to " + accountName +
              "\nNote that the interest per loan is " + (loanMarketplace.getInterestPerLoan() * 100) + "%";
    }
    return "Failed to borrow " + amount;
  }

  private String repay(CustomerID customer, String accountName, double amount) throws InvalidAmountException, InsufficientBalanceException, InvalidAccountException, IOException {
    Customer borrower = customers.get(customer.getKey());
    double realAmount = amount < borrower.getTotalRemainingDebt() ? amount : borrower.getTotalRemainingDebt();
    if (loanMarketplace.repayLoan(borrower.getUserID(), accountName, amount)) {
      return "You have repaid " + realAmount;
    }
    return "Failed to repaid " + amount;
  }

	
	/*// Only use for testing
	public void resetTestData() {
		addTestData();

		customersID = new ArrayList<>();
		customersID.add(new CustomerID("Bhagy"));
		customersID.add(new CustomerID("Christina"));
		customersID.add(new CustomerID("John"));
	}

	// Only use for testing
	public HashMap<String,Customer> getCustomers() {
		return customers;
	}*/


}

