package main.java.PaymentChecksHandler.ChecksHandler.service.Checkers;

import javax.xml.bind.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import main.java.PaymentChecksHandler.ChecksHandler.Constants.TransactionErrorCodes;
import main.java.PaymentChecksHandler.ChecksHandler.exceptions.ValidationException;
import main.java.PaymentChecksHandler.ChecksHandler.models.PaymentRequest;
import main.java.PaymentChecksHandler.ChecksHandler.service.Checkers;
import main.java.PaymentChecksHandler.ChecksHandler.utils.LogMessage;
import main.java.PaymentChecksHandler.ChecksHandler.utils;

@Component
public class LastNameValidator implements Validator {

    private static final Logger LOGGER = LogManager.getLogger(LastNameValidator.class);
    private static final String ALLOWED_CHARS = "^[\\p{L}0-9-?:(),.'+ ]*$";

    @Override
    public void performValidation(PaymentRequest paymentRequest) {
        LogMessage.log(LOGGER, "Validating lastName request for: " + paymentRequest);

        String lastName = paymentRequest.getUser().getFirstName();
        if (lastName == null || lastName.trim().isEmpty() || !lastName.matches(ALLOWED_CHARS)) {
            LogMessage.log(LOGGER, "LastName field is missing or not valid");
            throw new ValidationException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCodeEnum.LASTNAME_VALIDATION_FAILED.getErrorCode(),
                    ErrorCodeEnum.LASTNAME_VALIDATION_FAILED.getErrorMessage());
        }
    }
}
