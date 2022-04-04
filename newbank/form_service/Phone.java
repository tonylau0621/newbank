package newbank.form_service;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the validation rules and interaction message for the phone number input field.
 * 
 */
public class Phone extends Input {
    
    /** 
     * @return List<ValidationRule>
     */
    @Override
    public List<ValidationRule> getValidationRules() {
        List<ValidationRule> rules = new ArrayList<>();
        rules.add(new ValidationRule(Constant.PHONE_VALIDATION_REGEX, true, "The phone number is invalid."));
        return rules;
    }

    
    /** 
     * @return String
     */
    @Override
    public String getUserInteractionMessage() {
        return "Enter Phone";
    }
}
