package newbank.server;

public class MaxLoginAttemptReachException extends Exception {
    public MaxLoginAttemptReachException() { super("Login attempt reached. Please contact admin to unlock your user."); }
}
