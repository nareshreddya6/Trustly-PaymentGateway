package main.java.PaymentChecksHandler.ChecksHandler.service.Checkers;

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
public class EmailValidator implements Validator {

    private static final Logger LOGGER = LogManager.getLogger(EmailValidator.class);
    private static final String EMAIL_PATTERN = "(?i)[-a-zA-Z0-9+_][-a-zA-Z0-9+_.]*@[-a-zA-Z0-9][-a-zA-Z0-9.]*\\.[a-zA-Z]{2,30}";

    @Override
    public void performValidation(PaymentRequest paymentRequest) {
        LogMessage.log(LOGGER, "Validating email request for: " + paymentRequest);

        String email = paymentRequest.getUser().getEmail();
        if (email == null || email.trim().isEmpty() || !email.matches(EMAIL_PATTERN)) {
            LogMessage.log(LOGGER, "Invalid email format: " + email);
            throw new ValidationException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCodeEnum.EMAIL_VALIDATION_FAILED.getErrorCode(),
                    ErrorCodeEnum.EMAIL_VALIDATION_FAILED.getErrorMessage());
        }
    }
}
