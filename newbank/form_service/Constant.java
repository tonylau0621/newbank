package newbank.form_service;

/** 
 * Contains all public constants used by other classes.
 */
public class Constant {
    public static final int DEFAULT_MIN_LENGTH = 6;
    public static final int DEFAULT_MAX_LENGTH = 10;
    public static final int PASSWORD_MAX_LENGTH = 999;
    public static final String ALPHA_NUMERIC_REGEX = "[a-zA-Z\\d]+";
    public static final String SPECIAL_CHARACTER_REGEX = ".*[^a-zA-Z\\d]+.*";
    public static final String CAPITAL_LETTER_REGEX = ".*[A-Z]+.*";
    public static final String PHONE_VALIDATION_REGEX = "^(\\+\\d{1,3}( )?)?((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?){2}\\d{3}$|^(\\+\\d{1,3}( )?)?(\\d{3}[ ]?)(\\d{2}[ ]?){2}\\d{2}$";
    public static final String EMAIL_VALIDATION_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+.[a-zA-Z]{2,6}";
}
