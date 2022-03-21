package newbank.server;

public class InvalidInput extends Exception {
  public InvalidInput() {
    super("Invalid Input. Please try again.");
  }
}
