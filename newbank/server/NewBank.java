package newbank.server;

// Only user for testing
///mport java.util.ArrayList;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

// Please comment out all "Only use for testing" method/block/statement for real use.
public class NewBank {

	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;
	private ArrayList<Transaction> transactions;

	// Only use for testing
	//public ArrayList<CustomerID> customersID;

	private NewBank() {
		customers = DataHandler.readCustData();
		transactions = DataHandler.readTransation();
	}

	public static NewBank getBank() {
		return bank;
	}

	public synchronized CustomerID checkLogInDetails(String userName, String password) throws InvalidUserNameException, InvalidPasswordException {
		boolean isValidUserName = (customers.containsKey(userName));
		if(!isValidUserName) throw new InvalidUserNameException();
		Customer targetCustomer = customers.get(userName);
		CustomerID targetCustomerId = new CustomerID(userName);
		boolean isValidPassword = (targetCustomer.checkPassword(password));
		if(!isValidPassword) throw new InvalidPasswordException();
		return targetCustomerId;
	}


	// commands from the NewBank customer are processed in this method
	public synchronized Response processRequest(CustomerID customer, String request) throws IOException, InvalidAmountException, InsufficientBalanceException, InvalidAccountException {
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
            result += String.valueOf(i+1)+") "+ accounts.get(i).getAccount() + ": " + accounts.get(i).getAmount() + "\n";
        }
		return result;
	}

	private String newAccount(CustomerID customer, String accountName) {
		if(customers.keySet().contains(customer.getKey())){
			customers.get(customer.getKey()).addAccount(new Account("test", accountName, 0.0));
			return "SUCCESS";
		}
		return "FAIL";
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
		DataHandler.updateCustomer(customers);
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
			//return "Please check Accounts";
			return "FAIL";
		}
		//Customers should not be able to transfer less than 0.01
		if(amount < 0.01) {return "FAIL";}

		//get Account Balance Amount
		double balance = payer.getAccount(payingAccount).getAmount();
		//Is there enough balance?
		if(balance >= amount) {
			//Adjust balance for both accounts
			payer.getAccount(payingAccount).updateBalance(-amount);
			receiver.getAccount(receivingAccount).updateBalance(amount);

			//updated_balance = payer.getAccount(payingAccount).getAmount();

			//return "Payment was successful.";
			return "SUCCESS";
		}
		else {
			//return "Insufficient balance.";
			return "FAIL";
		}

	}

	public Customer getCustomer(CustomerID id){
		return customers.get(id.getKey());
	}

	public Response addCustomer(String username, String password, String firstName, String lastName, String phone, String email, String address) throws InvalidUserNameException, IOException {
        if (username.matches("[a-zA-Z0-9_-]{5,20}") || customers.keySet().contains(username)) {
            throw new InvalidUserNameException();
        }
        String userID = generateUserID(customers);
        Customer customer = new Customer(userID, password, firstName, lastName, phone, email, address);
        customer.addAccount(new Account("test", "Main", 0.0));
        customers.put(username, customer);
		Response response = new Response();
		response.setResponseMessage("Registration completed. Please Login with your username and password");
		DataHandler.updateCustomer(customers);
		return response;
        //addCustomerToDatabase(userID, customer);
    }

    
    // If all 99999999 userIDs are occupied, this method will perform infinite loop.
    // Can be changed to private for real use.
    public static String generateUserID(HashMap<String,Customer> customers) {
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

	private void addTransaction(Transaction transaction) throws IOException{
		transactions.add(transaction);
		DataHandler.updateTransaction(this.transactions);
	}

	
	/*// Only use for testing
	public void resetTestData() {
		Customer bhagy = new Customer("00243584", "bhagyPass", "Bhagy", "Brown", "07654321987", "bhagyishappy@gmail.com", "123 Wonder Street, London AB1 2YZ");
		bhagy.addAccount(new Account("Main", 1000.0));
		bhagy.addAccount(new Account("TestingAccount1", 1000.0));
		bhagy.addAccount(new Account("TestingAccount2", 1000.0));
		bhagy.addAccount(new Account("Savings", 1000.0));
		bhagy.addAccount(new Account("Investment", 1000.0));
		bhagy.addAccount(new Account("Current", 1000.0));
		customers.put("Bhagy", bhagy);

		Customer christina = new Customer("18392702", "christinaPass", "", "", "", "", "");
		christina.addAccount(new Account("Main", 1500.0));
		christina.addAccount(new Account("Savings", 1500.0));
		customers.put("Christina", christina);

		Customer john = new Customer("60023945", "johnPass", "", "", "", "", "");
		john.addAccount(new Account("Main", 1200.0));
		john.addAccount(new Account("Checking", 250.0));
		customers.put("John", john);

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

