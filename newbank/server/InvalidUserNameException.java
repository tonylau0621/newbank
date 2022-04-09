package newbank.server;

/**
 * Exception thrown when an invalid username is entered.
 */
public class InvalidUserNameException extends Exception {
  public InvalidUserNameException() {
    super("Username is invalid.");
  }

  public InvalidUserNameException(String msg){
    super(msg);
  }
}
