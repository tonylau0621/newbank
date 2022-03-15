package newbank.server;

public class InvalidUserNameException extends Exception {
  public InvalidUserNameException() {
    super("username is invalid.");
  }
}