package newbank.server;

public class InsufficientBalanceException extends Exception{
    public InsufficientBalanceException() {
        super("InsufficientBalance in your account");
      }
}
