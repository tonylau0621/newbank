package newbank.server;

// Only user for testing
//import java.util.ArrayList;
import java.util.HashMap;

// Please comment out all "Only use for testing" method/block/statement for real use.
public class NewBank {

	private static final NewBank bank = new NewBank();
	private HashMap<String,Customer> customers;

	// Only use for testing
	//public ArrayList<CustomerID> customersID;

	private NewBank() {
		customers = new HashMap<>();
		addTestData();
	}

	private void addTestData() {
		CustomerID bhagyID = new CustomerID("Bhagy", "bhagyPass", "Bhagy", "Brown", "07654321987", "bhagyishappy@gmail.com", "123 Wonder Street, London AB1 2YZ");
		Customer bhagy = new Customer(bhagyID);
		bhagy.addAccount(new Account("Main", 1000.0));
		customers.put(bhagy.getCustomerID().getKey(), bhagy);


		CustomerID christinaID = new CustomerID("Christina", "christinaPass");
		Customer christina = new Customer(christinaID);
		christina.addAccount(new Account("Savings", 1500.0));
		customers.put(christina.getCustomerID().getKey(), christina);

		CustomerID johnID = new CustomerID("John", "johnPass");
		Customer john = new Customer(johnID);
		john.addAccount(new Account("Checking", 250.0));
		customers.put(john.getCustomerID().getKey(), john);
	}

	public static NewBank getBank() {
		return bank;
	}

	public synchronized CustomerID checkLogInDetails(String userName, String password) throws InvalidUserNameException, InvalidPasswordException {
		boolean isValidUserName = (customers.containsKey(userName));
		if(!isValidUserName) throw new InvalidUserNameException();
		Customer targetCustomer = customers.get(userName);
		CustomerID targetCustomerId = targetCustomer.getCustomerID();
		boolean isValidPassword = (targetCustomerId.checkPassword(password));
		if(!isValidPassword) throw new InvalidPasswordException();
		return targetCustomerId;
	}

	// commands from the NewBank customer are processed in this method
	public synchronized String processRequest(CustomerID customer, String request) {
		String[] requestTokens = request.split("\\s+");

		if(customers.containsKey(customer.getKey())) {
			switch(requestTokens[0]) {
				case "SHOWMYACCOUNTS" : return showMyAccounts(customer);
				case "NEWACCOUNT" :
					if (requestTokens.length > 1) {
						return newAccount(customer, requestTokens[1]);
					}
				case "MOVE" :
					if (requestTokens.length > 3) {
						try {
							return moveAmount(Double.parseDouble(requestTokens[1]), requestTokens[2], requestTokens[3], customer);
						} catch (NumberFormatException e) {
							return "FAIL";
						}
					}
				case "PAY" :
					if (requestTokens.length > 4) {
						try {
							return payAmount(Double.parseDouble(requestTokens[1]), customer, requestTokens[2],
									(requestTokens[3]),requestTokens[4]);}
						catch (Exception e) {
							return "FAIL";
						}
					}
				default : return "FAIL";
			}
		}
		return "FAIL";
	}

	private String showMyAccounts(CustomerID customer) {
		return (customers.get(customer.getKey())).accountsToString();
	}

	private String newAccount(CustomerID customer, String accountName) {
		if(customers.keySet().contains(customer.getKey())){
			customers.get(customer.getKey()).addAccount(new Account(accountName, 0.0));
			return "SUCCESS";
		}
		return "FAIL";
	}

	private String moveAmount(double amount, String from, String to, CustomerID customer){
		//Account does not exist
		Customer target = customers.get(customer.getKey());
		if (target.getAccount(from) == null || target.getAccount(to) == null){
			//return "Account does not exist";
			return "FAIL";
		}
		//Not enough balance
		if (target.getAccount(from).getAmount() < amount){
			//return "Not enough amount in your account";
			return "FAIL";
		}
		//Update the amount and return success
		target.getAccount(from).updateBalance(-amount);
		target.getAccount(to).updateBalance(amount);
		return "SUCCESS";
	}

	// PAY command
	//Do I want to use Accounts or Customers?
	// payAmount: Takes two accounts and an amount as input
	private String payAmount(double amount, CustomerID payingCustomer, String payingAccount,
							 String receivingCustomerKey, String receivingAccount ) {
		//Account does not exist
		Customer payer = customers.get(payingCustomer.getKey());
		Customer receiver = customers.get(receivingCustomerKey);

		if (payer.getAccount(payingAccount) == null || receiver.getAccount(receivingAccount) == null) {
			//return "Please check Accounts";
			return "FAIL";
		}
		//Customers should not be able to transfer less than 0.01
		if(amount < 0.01) {return "FAIl";}

		//get Account Balance Amount
		double balance = payer.getAccount(payingAccount).getAmount();
		//Is there enough balance?
		if(balance > amount) {
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

	/*
	// Only use for testing
	public void resetTestData() {
		customers = new HashMap<>();
		CustomerID bhagyID = new CustomerID("Bhagy", "bhagyPass", "Bhagy", "Brown", "07654321987", "bhagyishappy@gmail.com", "123 Wonder Street, London AB1 2YZ");
		Customer bhagy = new Customer(bhagyID);
		bhagy.addAccount(new Account("Main", 1000.0));
		customers.put(bhagy.getCustomerID().getKey(), bhagy);


		CustomerID christinaID = new CustomerID("Christina", "christinaPass");
		Customer christina = new Customer(christinaID);
		christina.addAccount(new Account("Savings", 1500.0));
		customers.put(christina.getCustomerID().getKey(), christina);

		CustomerID johnID = new CustomerID("John", "johnPass");
		Customer john = new Customer(johnID);
		john.addAccount(new Account("Checking", 250.0));
		customers.put(john.getCustomerID().getKey(), john);

		customersID = new ArrayList<>();
		customersID.add(bhagyID);
		customersID.add(christinaID);
		customersID.add(johnID);
	}

	// Only use for testing
	public HashMap<String,Customer> getCustomers() {
		return customers;
	}
	*/

}

