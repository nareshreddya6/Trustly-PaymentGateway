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
public class PaymentRequestVal implements Validator {

    private static final Logger LOGGER = LogManager.getLogger(PaymentRequestValidator.class);

    @Override
    public void performValidation(PaymentRequest paymentRequest) {
        LogMessage.log(LOGGER, "Validating PAYMENT request for -> " + paymentRequest);

        // Check if paymentRequest is null
        if (isNull(paymentRequest)) {
            logAndThrowValidationException("Payment request is null", ErrorCodeEnum.PAYMENT_REQUEST_VALIDATION_FAILED);
        }

        // Check if the payment field within paymentRequest is null
        if (isNull(paymentRequest.getPayment())) {
            logAndThrowValidationException("Payment is null", ErrorCodeEnum.PAYMENT_VALIDATION_FAILED);
        }

        // Check if the user field within paymentRequest is null
        if (isNull(paymentRequest.getUser())) {
            logAndThrowValidationException("User is null", ErrorCodeEnum.USER_VALIDATION_FAILED);
        }
    }

    // Helper method to check for null
    private boolean isNull(Object obj) {
        return obj == null;
    }

    // Helper method for logging and throwing validation exceptions
    private void logAndThrowValidationException(String message, ErrorCodeEnum errorCodeEnum) {
        LogMessage.log(LOGGER, message);
        throw new ValidationException(HttpStatus.BAD_REQUEST,
                errorCodeEnum.getErrorCode(),
                errorCodeEnum.getErrorMessage());
    }
}
