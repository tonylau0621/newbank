package newbank.server;

public class InvalidAmountException extends Exception  {
    public InvalidAmountException() {
        super("Invalid amount entered");
      }
}
