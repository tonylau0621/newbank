package newbank.server;

/**
 * Exception thrown when the given account details are not valid.
 */
public class InvalidAccountException extends Exception{
    public InvalidAccountException() {
        super("Invalid account entered");
    }
}
