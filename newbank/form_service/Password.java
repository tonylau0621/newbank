package newbank.form_service;

import java.util.ArrayList;
import java.util.List;

public class Password extends Input {
    
    /** 
     * @return List<ValidationRule>
     */
    @Override
    public List<ValidationRule> getValidationRules() {
        List<ValidationRule> rules = new ArrayList<>();
        rules.add(new ValidationRule(Constant.CAPITAL_LETTER_REGEX, true, "Password must contain of at least 1 capital letter"));
        rules.add(new ValidationRule(Constant.SPECIAL_CHARACTER_REGEX, true, "Password must contain of at least 1 special character"));
        rules.add(new ValidationRule(Constant.DEFAULT_MIN_LENGTH, Constant.PASSWORD_MAX_LENGTH, true, "Password must be at least 6 characters"));
        return rules;
    }

    
    /** 
     * @return String
     */
    @Override
    public String getUserInteractionMessage() {
        return "Enter Password";
    }
}
