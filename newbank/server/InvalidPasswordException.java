package newbank.server;

/**
 * Exception thrown when an invalid password is entered.
 */
public class InvalidPasswordException extends Exception {
  public InvalidPasswordException() {
    super("Password is invalid.");
  }
}
