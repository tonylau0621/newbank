package newbank.server;

public class InvalidUserNameException extends Exception {
  public InvalidUserNameException() {
    super("Username is invalid.");
  }
}
