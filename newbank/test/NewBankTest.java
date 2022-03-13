package newbank.test;

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
    String accountType = command.split("\\s+")[1];
    Assertions.assertTrue(customers.get(customerID.getKey()).accountsToString().contains(accountType));
  }

  @ParameterizedTest
  @MethodSource("newbank.test.TestingData#provideCustomerIDAndInvalidNewAccountCommand")
  public void invalidNewAccountCommand(CustomerID customerID, String command) {
    bank.processRequest(customerID, command);
    if (command.split("\\s+").length > 1) {
      String accountType = command.split("\\s+")[1];
      Assertions.assertFalse(customers.get(customerID.getKey()).accountsToString().contains(accountType));
    } else {
      return;
    }

  }

  @Test
  public void validMoveCommand() {
    fail("The test has not been implemented yet.");
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
