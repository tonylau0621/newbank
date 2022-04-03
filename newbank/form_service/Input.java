package newbank.form_service;

import newbank.server.CommunicationService;

import java.io.IOException;
import java.util.List;

abstract class Input {
    public abstract List<ValidationRule> getValidationRules();
    public abstract String getUserInteractionMessage();
    public String getInput() throws IOException {
        String result;
        do {
            CommunicationService.sendOut(this.getUserInteractionMessage());
            result = CommunicationService.readIn();
        } while(!this.isInputValid(result));
        return result;
    }

    public boolean isInputValid(String inputString) {
        if(inputString == null || inputString.isEmpty()) return false;
        boolean result = true;
        List<ValidationRule> rules = this.getValidationRules();
        for(ValidationRule rule : rules) {
            if(!rule.isValid(inputString)) {
                CommunicationService.sendOut(rule.getErrorMessage());
                result = false;
                break;
            }
        }
        return result;
    }

}
