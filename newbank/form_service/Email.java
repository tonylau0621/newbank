package newbank.form_service;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the validation rules and interaction message for the email input field.
 * 
 */
public class Email extends Input {
    
    /** 
     * @return List<ValidationRule>
     */
    @Override
    public List<ValidationRule> getValidationRules() {
        List<ValidationRule> rules = new ArrayList<>();
        rules.add(new ValidationRule(Constant.EMAIL_VALIDATION_REGEX, true, "The email is invalid."));
        return rules;
    }

    
    /** 
     * @return String
     */
    @Override
    public String getUserInteractionMessage() {
        return "Enter Email";
    }
}
