package newbank.form_service;

public class ValidationRule {
    private String regex;
    private String errorMessage;
    private int minLength;
    private int maxLength;
    private boolean shouldMatch;
    public ValidationRule(String regex, boolean shouldMatch, String errorMessage) {
        this.regex = regex;
        this.shouldMatch = shouldMatch;
        this.errorMessage = errorMessage;
    }

    public ValidationRule(int minLength, int maxLength, boolean shouldMatch, String errorMessage) {
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.shouldMatch = shouldMatch;
        this.errorMessage = errorMessage;
    }

    
    /** 
     * @param inputString
     * @return boolean
     */
    public boolean isValid(String inputString) {
        boolean result;
        if(this.regex != null && !this.regex.isEmpty()) result = inputString.matches(this.regex) == this.shouldMatch;
        else result = inputString.length() >= minLength && inputString.length() <= maxLength;
        return result;
    }

    public String getErrorMessage() { return this.errorMessage; }
}
