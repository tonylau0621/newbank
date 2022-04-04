package newbank.form_service;

import java.util.ArrayList;
import java.util.List;

public class UserName extends Input {
    
    /** 
     * @return List<ValidationRule>
     */
    @Override
    public List<ValidationRule> getValidationRules() {
        List<ValidationRule> rules = new ArrayList<>();
        rules.add(new ValidationRule(Constant.ALPHA_NUMERIC_REGEX, true, "Username must be contains only alphabet and number"));
        rules.add(new ValidationRule(Constant.DEFAULT_MIN_LENGTH, Constant.DEFAULT_MAX_LENGTH, true, "Username must be in 6-10 characters"));
        return rules;
    }

    
    /** 
     * @return String
     */
    @Override
    public String getUserInteractionMessage() {
        return "Enter Username";
    }
}
