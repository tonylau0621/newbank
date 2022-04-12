package newbank.server;

public class SessionTimeoutException extends Exception {
    public SessionTimeoutException() { super("session timeout"); }
}
