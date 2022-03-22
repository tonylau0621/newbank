package newbank.server;

public class InvalidAccountException extends Exception{
    public InvalidAccountException() {
        super("Invalid account entered");
    }
}
