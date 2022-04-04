package newbank.server;

/**
 * Exception thrown when a customer tries to perform an action with more money than they have access to.
 */
public class InsufficientBalanceException extends Exception{
    public InsufficientBalanceException() {
        super("Insufficient balance in your account");
      }
}
