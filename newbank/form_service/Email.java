package newbank.form_service;

import java.util.ArrayList;
import java.util.List;

public class Email extends Input {
    @Override
    public List<ValidationRule> getValidationRules() {
        List<ValidationRule> rules = new ArrayList<>();
        rules.add(new ValidationRule(Constant.EMAIL_VALIDATION_REGEX, true, "The email is invalid."));
        return rules;
    }

    @Override
    public String getUserInteractionMessage() {
        return "Enter Email";
    }
}
