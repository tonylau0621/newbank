package newbank.server;

public class InvalidPasswordException extends Exception {
    public InvalidPasswordException() {
        super("password is invalid.");
    }
}
