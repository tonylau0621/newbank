package newbank.test;

import newbank.server.Customer;
import newbank.server.CustomerID;
import newbank.server.NewBank;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomerIDTest {
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
  public void testCustomerIDCheckPasswordMethodWithCorrectPassword(String username, String password) {
    Assertions.assertTrue(customers.get(username).getCustomerID().checkPassword(password));
  }

  @ParameterizedTest
  @MethodSource("newbank.test.TestingData#provideWrongUsernameAndPassword")
  public void testCustomerIDCheckPasswordMethodWithWrongPassword(String username, String password) {
    if (customers.get(username) != null) {
      Assertions.assertFalse(customers.get(username).getCustomerID().checkPassword(password));
    }
  }

  @ParameterizedTest
  @MethodSource("newbank.test.TestingData#providUserNameAndCorrectOldPasswordAndNewPassword")
  public void testCustomerIDsetPasswordMethodWithCorrectOldPassword(String username, String oldPassword, String newPassword) {
    Assertions.assertTrue(customers.get(username).getCustomerID().setPassword(oldPassword, newPassword));
    Assertions.assertTrue(customers.get(username).getCustomerID().checkPassword(newPassword));
  }

  @ParameterizedTest
  @MethodSource("newbank.test.TestingData#providUserNameAndWrongOldPasswordAndNewPassword")
  public void testCustomerIDsetPasswordMethodWithWrongOldPassword(String username, String oldPassword, String newPassword) {
    Assertions.assertFalse(customers.get(username).getCustomerID().setPassword(oldPassword, newPassword));
    //Assertions.assertFalse(customers.get(username).getCustomerID().checkPassword(newPassword));
  }

}
