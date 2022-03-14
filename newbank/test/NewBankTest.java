package newbank.test;

import newbank.server.Account;
import newbank.server.Customer;
import newbank.server.CustomerID;
import newbank.server.NewBank;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.fail;

public class NewBankTest {

  private static NewBank bank;
  private static HashMap<String, Customer> customers;
  private static ArrayList<CustomerID> customersID;

  @BeforeAll
  public static void setup() {
  }

  @BeforeEach
  public void setupEachTime() {
    TestingData.setup();
    bank = TestingData.bank;
    customers = TestingData.customers;
    customersID = TestingData.customersID;
  }

  // Tests for login
  @ParameterizedTest
  @MethodSource("newbank.test.TestingData#provideCorrectUsernameAndPassword")
  public void logInWithCorrectUsernameAndPassword(String username, String password) {
    CustomerID customerID = bank.checkLogInDetails(username, password);
    Assertions.assertNotNull(customerID);
    Assertions.assertEquals(username, customerID.getKey());
  }

  @ParameterizedTest
  @MethodSource("newbank.test.TestingData#provideWrongUsernameAndPassword")
  public void logInWithWrongUsernameAndPassword(String username, String password) {
    CustomerID customerID = bank.checkLogInDetails(username, password);
    Assertions.assertNull(customerID);
  }
  // End of Tests for login


  // Tests for NEWACCOUNT
  @ParameterizedTest
  @MethodSource("newbank.test.TestingData#provideCustomerIDAndValidNewAccountCommand")
  public void validNewAccountCommand(CustomerID customerID, String command) {
    Assertions.assertEquals("SUCCESS", bank.processRequest(customerID, command));
    Customer customer = customers.get(customerID.getKey());
    ArrayList<Account> accounts = customer.getAccounts();
    String accountType = command.split("\\s+")[1];
    for (int i = 0; i < accounts.size(); i++) {
      if (accounts.get(i).getAccountName().equals(accountType) && accounts.get(i).getOpeningBalance() == 0.0) {
        return;
      }
    }
    fail(customerID.getFirstName() + "'s " + accountType + " account not found/not probably set.");
  }

  @ParameterizedTest
  @MethodSource("newbank.test.TestingData#provideCustomerIDAndInvalidNewAccountCommand")
  public void invalidNewAccountCommand(CustomerID customerID, String command) {
    Customer customer = customers.get(customerID.getKey());

    ArrayList<Account> accountsBeforeCommand = customer.getAccounts();

    /*
    // for deep copy
    ArrayList<Account> accountsBeforeCommand = new ArrayList<>();
    for (int i = 0; i < customer.getAccounts().size(); i++) {
      accountsBeforeCommand.add(new Account(customer.getAccounts().get(i).getAccountName(), customer.getAccounts().get(i).getOpeningBalance()));
    }
    */

    Assertions.assertEquals("FAIL", bank.processRequest(customerID, command));
    ArrayList<Account> accountsAfterCommand = customer.getAccounts();
    Assertions.assertEquals(accountsBeforeCommand.size(), accountsAfterCommand.size());
    for (int i = 0; i < accountsBeforeCommand.size(); i++) {
      Assertions.assertEquals(accountsBeforeCommand.get(i).getAccountName(), accountsAfterCommand.get(i).getAccountName());
      Assertions.assertEquals(accountsBeforeCommand.get(i).getOpeningBalance(), accountsAfterCommand.get(i).getOpeningBalance());
    }
  }
  // End of tests for NEWACCOUNT


  // Tests for MOVE
  // Assume customer has the corresponding accounts and enough balance.
  @ParameterizedTest
  @MethodSource("newbank.test.TestingData#provideCustomerIDAndValidMoveCommand")
  public void validMoveCommand(CustomerID customerID, String command) {
    String[] commands = command.split("\\s+");
    Double amount = Double.parseDouble(commands[1]);
    String account1Name = commands[2];
    String account2Name = commands[3];
    Customer customer = customers.get(customerID.getKey());
    ArrayList<Account> accounts = customer.getAccounts();
    Account account1 = null;
    Account account2 = null;
    for (int i = 0; i < accounts.size(); i++) {
      if (accounts.get(i).getAccountName().equals(account1Name)) {
        account1 = accounts.get(i);
      }
      if (accounts.get(i).getAccountName().equals(account2Name)) {
        account2 = accounts.get(i);
      }
    }
    if (account1 == null) {
      account1 = new Account(account1Name, 9999.0);
      customer.addAccount(account1);
    }
    if (account2 == null) {
      account2 = new Account(account1Name, 9999.0);
      customer.addAccount(account2);
    }

    Double account1OldBalance = account1.getOpeningBalance();
    Double account2OldBalance = account2.getOpeningBalance();

    Assertions.assertEquals("SUCCESS", bank.processRequest(customerID, command));

    Assertions.assertEquals(account1OldBalance - amount, account1.getOpeningBalance());
    Assertions.assertEquals(account2OldBalance + amount, account2.getOpeningBalance());

  }

  @ParameterizedTest
  @MethodSource("newbank.test.TestingData#provideCustomerIDAndInvalidMoveCommand")
  public void invalidMoveCommand(CustomerID customerID, String command) {
    invalidNewAccountCommand(customerID, command);
  }

  // Assume customer has the corresponding accounts.
  @ParameterizedTest
  @MethodSource("newbank.test.TestingData#provideCustomerIDAndInvalidMoveCommandWithInvalidAmount")
  public void invalidMoveCommandWithInValidAmount(CustomerID customerID, String command) {
    String[] commands = command.split("\\s+");
    Double amount = Double.parseDouble(commands[1]);
    String account1Name = commands[2];
    String account2Name = commands[3];
    Customer customer = customers.get(customerID.getKey());
    ArrayList<Account> accounts = customer.getAccounts();
    Account account1 = null;
    Account account2 = null;
    for (int i = 0; i < accounts.size(); i++) {
      if (accounts.get(i).getAccountName().equals(account1Name)) {
        account1 = accounts.get(i);
      }
      if (accounts.get(i).getAccountName().equals(account2Name)) {
        account2 = accounts.get(i);
      }
    }
    if (account1 == null) {
      account1 = new Account(account1Name, 9999.0);
      customer.addAccount(account1);
    }
    if (account2 == null) {
      account2 = new Account(account1Name, 9999.0);
      customer.addAccount(account2);
    }

    Double account1OldBalance = account1.getOpeningBalance();
    Double account2OldBalance = account2.getOpeningBalance();

    Assertions.assertEquals("FAIL", bank.processRequest(customerID, command));

    Assertions.assertEquals(account1OldBalance, account1.getOpeningBalance());
    Assertions.assertEquals(account2OldBalance, account2.getOpeningBalance());

  }
  // End of tests for MOVE


  // Tests for PAY
  @ParameterizedTest
  @MethodSource("newbank.test.TestingData#provideCustomerIDAndValidPayCommand")
  public void validPayCommand(CustomerID customerID, String command) {
    String[] commands = command.split("\\s+");
    Customer payer = customers.get(customerID.getKey());
    ArrayList<Account> payerAccounts = payer.getAccounts();
    Customer receiver = customers.get(commands[1]);
    ArrayList<Account> receiverAccounts = receiver.getAccounts();
    Double amount = Double.parseDouble(commands[2]);

    Double payerOriginalTotal = calculateTotalAmount(payerAccounts);
    Double receiverOriginalTotal = calculateTotalAmount(receiverAccounts);

    Assertions.assertEquals("SUCCESS", bank.processRequest(customerID, command));

    payerAccounts = payer.getAccounts();
    receiverAccounts = receiver.getAccounts();
    Double payerNewTotal = calculateTotalAmount(payerAccounts);
    Double receiverNewTotal = calculateTotalAmount(receiverAccounts);

    Assertions.assertEquals(payerOriginalTotal - amount, payerNewTotal);
    Assertions.assertEquals(receiverOriginalTotal + amount, receiverNewTotal);
  }

  public Double calculateTotalAmount(ArrayList<Account> accounts) {
    Double totalAmount = 0.0;
    for (int i = 0; i < accounts.size(); i++) {
      totalAmount += accounts.get(i).getOpeningBalance();
    }
    return totalAmount;
  }

  @ParameterizedTest
  @MethodSource("newbank.test.TestingData#provideCustomerIDAndValidPayCommand")
  public void invalidPayCommand(CustomerID customerID, String command) {
    String[] commands = command.split("\\s+");
    Customer payer = customers.get(customerID.getKey());
    ArrayList<Account> payerAccounts = payer.getAccounts();
    if (commands.length < 3) {
      fail("The command format is not correct");
    }
    Customer receiver = customers.get(commands[1]);

    ArrayList<Account> receiverAccounts = receiver == null ? null : receiver.getAccounts();
    try {
      Double amount = Double.parseDouble(commands[2]);
    } catch (NumberFormatException e) {
      fail("Input amount is not a number");
    }

    Assertions.assertEquals("FAIL", bank.processRequest(customerID, command));


    ArrayList<Account> payerAccountsAfterCommand = payer.getAccounts();
    Assertions.assertEquals(payerAccounts.size(), payerAccountsAfterCommand.size());
    for (int i = 0; i < payerAccounts.size(); i++) {
      Assertions.assertEquals(payerAccounts.get(i).getAccountName(), payerAccountsAfterCommand.get(i).getAccountName());
      Assertions.assertEquals(payerAccounts.get(i).getOpeningBalance(), payerAccountsAfterCommand.get(i).getOpeningBalance());
    }

    if (receiver != null) {
      ArrayList<Account> receiverAccountsAfterCommand = receiver.getAccounts();
      Assertions.assertEquals(receiverAccounts.size(), receiverAccountsAfterCommand.size());
      for (int i = 0; i < receiverAccounts.size(); i++) {
        Assertions.assertEquals(receiverAccounts.get(i).getAccountName(), receiverAccountsAfterCommand.get(i).getAccountName());
        Assertions.assertEquals(receiverAccounts.get(i).getOpeningBalance(), receiverAccountsAfterCommand.get(i).getOpeningBalance());
      }
    }
  }
  // End of tests for PAY


  @AfterEach
  public void setupAfterEachTime() {
  }

  @AfterAll
  public static void setupAfterAll() {
  }

}
