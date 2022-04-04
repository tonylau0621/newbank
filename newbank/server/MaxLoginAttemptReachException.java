package newbank.server;

/**
 * Exception thrown when a user makes too many failed attempts to login.
 */
public class MaxLoginAttemptReachException extends Exception {
    public MaxLoginAttemptReachException() { super("Login attempt reached. Please contact admin to unlock your user."); }
}
