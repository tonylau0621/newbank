package newbank.form_service;

import newbank.server.CommunicationService;
import newbank.server.SessionTimeoutException;

import java.io.IOException;
import java.util.List;

/**
 * Abstract class for all input fields.
 * Holds the default validation rules and the message to be displayed to the user, to be overridden by subclasses.
 * 
 */
abstract class Input {
    public abstract List<ValidationRule> getValidationRules();
    public abstract String getUserInteractionMessage();
    
    /** 
     * @return String
     * @throws IOException
     */
    public String getInput() throws IOException, SessionTimeoutException {
        String result;
        do {
            CommunicationService.sendOut(this.getUserInteractionMessage());
            result = CommunicationService.readIn();
        } while(!this.isInputValid(result));
        return result;
    }

    
    /** 
     * @param inputString
     * @return boolean
     */
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
