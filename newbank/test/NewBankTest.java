package newbank.test;

import newbank.server.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
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
    try {
      CustomerID customerID = bank.checkLogInDetails(username, password);
      Assertions.assertEquals(username, customerID.getKey());
    } catch (InvalidUserNameException | InvalidPasswordException e) {
      fail("Correct username and password but login fail");
    }
  }

  @ParameterizedTest
  @MethodSource("newbank.test.TestingData#provideWrongUsernameAndPassword")
  public void logInWithWrongUsernameAndPassword(String username, String password) {
    try {
      CustomerID customerID = bank.checkLogInDetails(username, password);
    } catch (InvalidUserNameException | InvalidPasswordException e) {
      return;
    }
    fail("Logged in with wrong username and/or password");
  }
  // End of Tests for login


  // Tests for NEWACCOUNT
  @ParameterizedTest
  @MethodSource("newbank.test.TestingData#provideCustomerIDAndValidNewAccountCommand")
  public void validNewAccountCommand(CustomerID customerID, String command) {
    Response processRequestResult = new Response();
    try {
      processRequestResult = bank.processRequest(customerID, command);
    } catch (IOException | InvalidAmountException | InsufficientBalanceException | InvalidAccountException e) {
      e.printStackTrace();
    }
    Assertions.assertEquals("SUCCESS", processRequestResult.getResponseMessage());
    Customer customer = customers.get(customerID.getKey());
    ArrayList<Account> accounts = customer.getAccounts();
    String accountType = command.split("\\s+")[1];
    for (int i = 0; i < accounts.size(); i++) {
      if (accounts.get(i).getAccount().equals(accountType) && accounts.get(i).getAmount() == 0.0) {
        return;
      }
    }
    fail(customer.getFirstName() + "'s " + accountType + " account not found/not probably set.");
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
      accountsBeforeCommand.add(new Account(customer.getAccounts().get(i).getAccount(), customer.getAccounts().get(i).getAmount()));
    }
    */
    Response processRequestResult = new Response();
    try {
      try {
        processRequestResult = bank.processRequest(customerID, command);
      } catch (InvalidAmountException | InsufficientBalanceException | InvalidAccountException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    Assertions.assertEquals("FAIL", processRequestResult.getResponseMessage());
    ArrayList<Account> accountsAfterCommand = customer.getAccounts();
    Assertions.assertEquals(accountsBeforeCommand.size(), accountsAfterCommand.size());
    for (int i = 0; i < accountsBeforeCommand.size(); i++) {
      Assertions.assertEquals(accountsBeforeCommand.get(i).getAccount(), accountsAfterCommand.get(i).getAccount());
      Assertions.assertEquals(accountsBeforeCommand.get(i).getAmount(), accountsAfterCommand.get(i).getAmount());
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
    Account account1 = customer.getAccount(account1Name);
    Account account2 = customer.getAccount(account2Name);
    if (account1 == null) {
      account1 = new Account("test",account1Name, 9999.0);
      customer.addAccount(account1);
    }
    if (account2 == null) {
      account2 = new Account("test",account2Name, 9999.0);
      customer.addAccount(account2);
    }

    Double account1OldBalance = account1.getAmount();
    Double account2OldBalance = account2.getAmount();
    Response processRequestResult = new Response();
    try {
      processRequestResult = bank.processRequest(customerID, command);
    } catch (IOException | InvalidAmountException | InsufficientBalanceException | InvalidAccountException e) {
      e.printStackTrace();
    }
    Assertions.assertEquals("SUCCESS", processRequestResult.getResponseMessage());

    Assertions.assertEquals(account1OldBalance - amount, account1.getAmount());
    Assertions.assertEquals(account2OldBalance + amount, account2.getAmount());

  }

  /*@ParameterizedTest
  @MethodSource("newbank.test.TestingData#provideCustomerIDAndInvalidMoveCommand")
  public void invalidMoveCommand(CustomerID customerID, String command) {
    invalidNewAccountCommand(customerID, command);
  }
  */

  // Assume customer has the corresponding accounts.
  @ParameterizedTest
  @MethodSource("newbank.test.TestingData#provideCustomerIDAndInvalidMoveCommandWithInvalidAmount")
  public void invalidMoveCommandWithInvalidAmount(CustomerID customerID, String command) {
    String[] commands = command.split("\\s+");
    Double amount = Double.parseDouble(commands[1]);
    String account1Name = commands[2];
    String account2Name = commands[3];
    Customer customer = customers.get(customerID.getKey());
    Account account1 = customer.getAccount(account1Name);
    Account account2 = customer.getAccount(account2Name);
    if (account1 == null) {
      account1 = new Account("test", account1Name, 9999.0);
      customer.addAccount(account1);
    }
    if (account2 == null) {
      account2 = new Account("test",account2Name, 9999.0);
      customer.addAccount(account2);
    }

    Double account1OldBalance = account1.getAmount();
    Double account2OldBalance = account2.getAmount();
    Response processRequestResult = new Response();
    try {
      processRequestResult = bank.processRequest(customerID, command);
    } catch (IOException | InvalidAmountException | InsufficientBalanceException | InvalidAccountException e) {
      e.printStackTrace();
    }
    Assertions.assertEquals("FAIL", processRequestResult.getResponseMessage());

    Assertions.assertEquals(account1OldBalance, account1.getAmount());
    Assertions.assertEquals(account2OldBalance, account2.getAmount());

  }
  // End of tests for MOVE


  // Tests for PAY
  @ParameterizedTest
  @MethodSource("newbank.test.TestingData#provideCustomerIDAndValidPayCommand")
  public void validPayCommand(CustomerID customerID, String command) {
    String[] commands = command.split("\\s+");
    Customer payer = customers.get(customerID.getKey());
    Account payerAccount = payer.getAccount(commands[2]);
    Customer receiver = customers.get(commands[3]);
    Account receiverAccount = receiver.getAccount(commands[4]);
    Double amount = Double.parseDouble(commands[1]);

    Double payerAccountOriginalAmount = payerAccount.getAmount();
    Double receiverAccountOriginalAmount = receiverAccount.getAmount();
    Response processRequestResult = new Response();
    try {
      processRequestResult = bank.processRequest(customerID, command);
    } catch (IOException | InvalidAmountException | InsufficientBalanceException | InvalidAccountException e) {
      e.printStackTrace();
    }
    Assertions.assertEquals("SUCCESS", processRequestResult.getResponseMessage());

    Double payerAccountNewAmount = payerAccount.getAmount();
    Double receiverAccountNewAmount = receiverAccount.getAmount();

    if (payerAccount != receiverAccount) {
      Assertions.assertEquals(payerAccountOriginalAmount - amount, payerAccountNewAmount);
      Assertions.assertEquals(receiverAccountOriginalAmount + amount, receiverAccountNewAmount);
    } else {
      Assertions.assertEquals(payerAccountOriginalAmount, payerAccountNewAmount);
      Assertions.assertEquals(receiverAccountOriginalAmount, receiverAccountNewAmount);
    }
  }

  /*
  public Double calculateTotalAmount(ArrayList<Account> accounts) {
    Double totalAmount = 0.0;
    for (int i = 0; i < accounts.size(); i++) {
      totalAmount += accounts.get(i).getAmount();
    }
    return totalAmount;
  }
  */

  @ParameterizedTest
  @MethodSource("newbank.test.TestingData#provideCustomerIDAndInvalidPayCommand")
  public void invalidPayCommand(CustomerID customerID, String command) {
    String[] commands = command.split("\\s+");
    if (commands.length >= 5) {
      try {
        Customer payer = customers.get(customerID.getKey());
        Account payerAccount = payer.getAccount(commands[2]);
        Customer receiver = customers.get(commands[3]);
        if (receiver == null) return;
        Account receiverAccount = receiver.getAccount(commands[4]);

        if (payerAccount != null && receiverAccount != null) {
          Double payerAccountOriginalAmount = payerAccount.getAmount();
          Double receiverAccountOriginalAmount = receiverAccount.getAmount();
          Response processRequestResult = new Response();
          try {
            try {
              processRequestResult = bank.processRequest(customerID, command);
            } catch (InvalidAmountException | InsufficientBalanceException | InvalidAccountException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
          } catch (IOException e) {
            e.printStackTrace();
          }
          Assertions.assertEquals("FAIL", processRequestResult.getResponseMessage());

          Double payerAccountNewAmount = payerAccount.getAmount();
          Double receiverAccountNewAmount = receiverAccount.getAmount();

          Assertions.assertEquals(payerAccountOriginalAmount, payerAccountNewAmount);
          Assertions.assertEquals(receiverAccountOriginalAmount, receiverAccountNewAmount);
        }
      } catch (NumberFormatException e) {
        return;
      }
    }

  }
  // End of tests for PAY


  // Test for generateUserId method
  // Change the modifier of the method to public before test.
  @Test
  public void generateManyUniqueUserID() {
    String[] userIDs = new String[20000];
    for (int i = 0; i < userIDs.length; i++) {
      userIDs[i] = bank.generateUserID(customers);
      customers.put("testingUser"+i, new Customer(userIDs[i], "", "", "", "", "", ""));
      //System.out.println(userIDs[i]);
      for (int j = 0; j < i; j++) {
        if (userIDs[i].equals(userIDs[j])) {
          fail("There exists a duplicated user id. " + i);
        }
      }
    }
  }



  @AfterEach
  public void setupAfterEachTime() {
  }

  @AfterAll
  public static void setupAfterAll() {
  }

}
