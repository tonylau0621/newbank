package newbank.form_service;

import java.util.ArrayList;
import java.util.List;

public class Phone extends Input {
    @Override
    public List<ValidationRule> getValidationRules() {
        List<ValidationRule> rules = new ArrayList<>();
        rules.add(new ValidationRule(Constant.PHONE_VALIDATION_REGEX, true, "The phone number is invalid."));
        return rules;
    }

    @Override
    public String getUserInteractionMessage() {
        return "Enter Phone";
    }
}