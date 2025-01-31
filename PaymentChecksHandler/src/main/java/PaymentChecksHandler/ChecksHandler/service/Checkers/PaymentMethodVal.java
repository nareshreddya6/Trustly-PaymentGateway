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
public class PaymentMethodVal implements Validator {

    private static final Logger LOGGER = LogManager.getLogger(PaymentMethodValidator.class);

    private static final String APM = "APM";

    // A helper method for error logging and exception throwing
    private void logAndThrowValidationException(String message) {
        LogMessage.log(LOGGER, message);
        throw new ValidationException(HttpStatus.BAD_REQUEST,
                ErrorCodeEnum.PAYMENT_METHOD_VALIDATION_FAILED.getErrorCode(),
                ErrorCodeEnum.PAYMENT_METHOD_VALIDATION_FAILED.getErrorMessage());
    }

    @Override
    public void performValidation(PaymentRequest paymentRequest) {
        LogMessage.log(LOGGER, "Validating PAYMENT METHOD request for -> " + paymentRequest);

        // Use basic checks to verify the payment method string
        String paymentMethod = paymentRequest.getPayment().getPaymentMethod();

        if (isBlankOrInvalidMethod(paymentMethod)) {
            logAndThrowValidationException("Payment method field is missing or invalid.");
        }

        // Checking against the PaymentMethodEnum for valid payment method
        if (PaymentMethodEnum.getPaymentMethod(paymentMethod) == null) {
            logAndThrowValidationException("Payment method field is not recognized.");
        }
    }

    private boolean isBlankOrInvalidMethod(String paymentMethod) {
        return paymentMethod == null || paymentMethod.trim().isEmpty() || !paymentMethod.equalsIgnoreCase(APM);
    }
}
