package newbank.server;

public class InvalidPasswordException extends Exception {
    public InvalidPasswordException() {
        super("Password is invalid.");
    }
}
