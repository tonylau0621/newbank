package newbank.test;

import newbank.server.Account;
import newbank.server.Customer;
import newbank.server.CustomerID;
import newbank.server.NewBank;
import org.junit.jupiter.params.provider.Arguments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

public class TestingData {

  public static NewBank bank;
  public static HashMap<String, Customer> customers;
  public static ArrayList<CustomerID> customersID;


  public static void setup() {
    bank = NewBank.getBank();

    // copy customers from NewBank class.
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

  // Testing data
  public static Stream<Arguments> provideCorrectUsernameAndPassword() {
    return Stream.of(
            Arguments.of("Bhagy", "bhagyPass"),
            Arguments.of("Christina", "christinaPass"),
            Arguments.of("John", "johnPass")
    );
  }

  public static Stream<Arguments> provideWrongUsernameAndPassword() {
    return Stream.of(
            Arguments.of("Bhagy", "bhagyABC"),
            Arguments.of("Christina", "chrisPass"),
            Arguments.of("John", "12345"),
            Arguments.of("Bhagy", ""),
            Arguments.of("", ""),
            Arguments.of("", "bhagyPass"),
            Arguments.of("Christina christinaPass", ""),
            Arguments.of("", "John johnPass")
    );
  }

  public static Stream<Arguments> provideCustomerIDAndValidNewAccountCommand() {
    String[] commands = {"NEWACCOUNT Current", "NEWACCOUNT Investment", "NEWACCOUNT Pension"};
    Stream<Arguments> stream = Stream.of();
    for (int i = 0; i < customersID.size(); i++) {
      for (int j = 0; j < commands.length; j++) {
        stream = Stream.concat(stream, Stream.of(Arguments.of(customersID.get(i), commands[j])));
      }
    }
    return stream;
  }

  public static Stream<Arguments> provideCustomerIDAndInvalidNewAccountCommand() {
    String[] commands = {"NEWACCOUNT", "ACCOUNTNEW Current", "Investment NEWACCOUNT"};
    Stream<Arguments> stream = Stream.of();
    for (int i = 0; i < customersID.size(); i++) {
      for (int j = 0; j < commands.length; j++) {
        stream = Stream.concat(stream, Stream.of(Arguments.of(customersID.get(i), commands[j])));
      }
    }
    return stream;
  }

  public static Stream<Arguments> providUserNameAndCorrectOldPasswordAndNewPassword() {
    return Stream.of(
            Arguments.of("Bhagy", "bhagyPass", "12345"),
            Arguments.of("Christina", "christinaPass", "abcde"),
            Arguments.of("John", "johnPass", "newPassJohn")
    );
  }

  // End of testing data

}
