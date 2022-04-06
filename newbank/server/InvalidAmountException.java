package newbank.server;

/**
 * Exception thrown when an invalid amount is entered for a given action.
 * Most amounts are expected to be handled as doubles.
 */
public class InvalidAmountException extends Exception  {
    public InvalidAmountException() {
        super("Invalid amount entered");
      }
}
