package newbank.test;

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

    // In order to do the test, please make "Only use for testing" method/block/statement in NewBank class available.
    // Also make the following available.
    bank.resetTestData();
    customers = bank.getCustomers();
    customersID = bank.customersID;
  }

  // Testing data

  // Data for login
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

  public static Stream<Arguments> provideUsernameAndCorrectOldPasswordAndNewPassword() {
    return Stream.of(
            Arguments.of("Bhagy", "bhagyPass", "12345"),
            Arguments.of("Christina", "christinaPass", "abcde"),
            Arguments.of("John", "johnPass", "newPassJohn")
    );
  }

  public static Stream<Arguments> provideUsernameAndWrongOldPasswordAndNewPassword() {
    return Stream.of(
            Arguments.of("Bhagy", "bhagyPass1", "12345"),
            Arguments.of("Christina", "passChristina", "abcde"),
            Arguments.of("John", "JohnPass", "newPassJohn")
    );
  }
  // End of data for login

  // Data for NEWACCOUNT
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
  // End of data for NEWACCOUNT

  // Data for MOVE
  // Assume customer has the corresponding accounts and enough balance.
  public static Stream<Arguments> provideCustomerIDAndValidMoveCommand() {
    String[] commands = {"MOVE 100 TestingAccount1 TestingAccount2", "MOVE 75 Main Savings", "MOVE 14.9 Investment Current"};
    Stream<Arguments> stream = Stream.of();
    for (int i = 0; i < customersID.size(); i++) {
      for (int j = 0; j < commands.length; j++) {
        stream = Stream.concat(stream, Stream.of(Arguments.of(customersID.get(i), commands[j])));
      }
    }
    return stream;
  }

  public static Stream<Arguments> provideCustomerIDAndInvalidMoveCommand() {
    String[] commands = {"MOVE 100 TestingAccount1", "MOVE 3ab Main Savings", "14.9 Investment Current", "MOVE Investment Current 170",
            "MOVE 300 NotExistsAccount1 Main", "MOVE 50 NotExistsAccount1 NotExistsAccount2"};
    Stream<Arguments> stream = Stream.of();
    for (int i = 0; i < customersID.size(); i++) {
      for (int j = 0; j < commands.length; j++) {
        stream = Stream.concat(stream, Stream.of(Arguments.of(customersID.get(i), commands[j])));
      }
    }
    return stream;
  }

  // Assume customer has the corresponding accounts.
  public static Stream<Arguments> provideCustomerIDAndInvalidMoveCommandWithInvalidAmount() {
    String[] commands = {"MOVE -100 TestingAccount1 TestingAccount2", "MOVE -26.9 Main Savings", "MOVE 0 Investment Current"};
    Stream<Arguments> stream = Stream.of();
    for (int i = 0; i < customersID.size(); i++) {
      for (int j = 0; j < commands.length; j++) {
        stream = Stream.concat(stream, Stream.of(Arguments.of(customersID.get(i), commands[j])));
      }
    }
    return stream;
  }
  //End of data for MOVE

  // Data for PAY
  // Assume customer has enough balance.
  public static Stream<Arguments> provideCustomerIDAndValidPayCommand() {
    //String[] commands = {"PAY John 100", "PAY Christina 36.7", "PAY Bhagy 0.08"};
    String[] commands = {"PAY 100 Main John Checking", "PAY 36.7 Main Bhagy Main", "PAY 0.08 Main Christina Savings"};
    Stream<Arguments> stream = Stream.of();
    for (int i = 0; i < customersID.size(); i++) {
      for (int j = 0; j < commands.length; j++) {
        stream = Stream.concat(stream, Stream.of(Arguments.of(customersID.get(i), commands[j])));
      }
    }
    return stream;
  }

  public static Stream<Arguments> provideCustomerIDAndInvalidPayCommand() {
    String[] commands = {"PAY John 9999999 Main Checking", "PAY 9999999 Main Bhagy Main", "PAY Christina -150", "PAY Bhagy 0", "PAY Peter 11.6", "PAY Christina -53.9", "PAY -100 Main John Checking"};
    Stream<Arguments> stream = Stream.of();
    for (int i = 0; i < customersID.size(); i++) {
      for (int j = 0; j < commands.length; j++) {
        stream = Stream.concat(stream, Stream.of(Arguments.of(customersID.get(i), commands[j])));
      }
    }
    return stream;
  }
  // End of data for PAY

  // End of testing data

}
