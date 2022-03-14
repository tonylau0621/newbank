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

  @ParameterizedTest
  @MethodSource("newbank.test.TestingData#provideCustomerIDAndValidNewAccountCommand")
  public void validNewAccountCommand(CustomerID customerID, String command) {
    bank.processRequest(customerID, command);
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

    // Deep copy
    ArrayList<Account> accountsBeforeCommand = new ArrayList<>();
    for (int i = 0; i < customer.getAccounts().size(); i++) {
      accountsBeforeCommand.add(new Account(customer.getAccounts().get(i).getAccountName(), customer.getAccounts().get(i).getOpeningBalance()));
    }

    bank.processRequest(customerID, command);
    ArrayList<Account> accountsAfterCommand = customer.getAccounts();
    Assertions.assertEquals(accountsBeforeCommand.size(), accountsAfterCommand.size());
    for (int i = 0; i < accountsBeforeCommand.size(); i++) {
      Assertions.assertEquals(accountsBeforeCommand.get(i).getAccountName(), accountsAfterCommand.get(i).getAccountName());
      Assertions.assertEquals(accountsBeforeCommand.get(i).getOpeningBalance(), accountsAfterCommand.get(i).getOpeningBalance());
    }
  }

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

    bank.processRequest(customerID, command);

    Assertions.assertEquals(account1OldBalance - amount, account1.getOpeningBalance());
    Assertions.assertEquals(account2OldBalance + amount, account2.getOpeningBalance());

  }

  @Test
  public void invalidMoveCommand() {
    fail("The test has not been implemented yet.");
  }

  @Test
  public void validPayCommand() {
    fail("The test has not been implemented yet.");
  }

  @Test
  public void invalidPayCommand() {
    fail("The test has not been implemented yet.");
  }

  @AfterEach
  public void setupAfterEachTime() {
  }

  @AfterAll
  public static void setupAfterAll() {
  }

}
