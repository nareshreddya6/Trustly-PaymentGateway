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
public class FirstNameVal implements Validator {
    private static final Logger LOGGER = LogManager.getLogger(FirstNameValidator.class);
    private static final String ALLOWED_CHARS = "^[\\p{L}0-9-?:(),.'+ ]*$";

    @Override
    public void performValidation(PaymentRequest paymentRequest) {
        LogMessage.log(LOGGER, "Validating firstName request for: " + paymentRequest);

        String firstName = paymentRequest.getUser().getFirstName();
        if (firstName == null || firstName.trim().isEmpty() || !firstName.matches(ALLOWED_CHARS)) {
            LogMessage.log(LOGGER, "FirstName field is missing or not valid");
            throw new ValidationException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCodeEnum.FIRSTNAME_VALIDATION_FAILED.getErrorCode(),
                    ErrorCodeEnum.FIRSTNAME_VALIDATION_FAILED.getErrorMessage());
        }
    }
}
