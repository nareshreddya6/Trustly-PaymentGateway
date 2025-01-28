package main.java.PaymentChecksHandler.ChecksHandler.service.Checkers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import main.java.PaymentChecksHandler.ChecksHandler.constants.TransactionErrorCodes;
import main.java.PaymentChecksHandler.ChecksHandler.Exceptions.ValidationException;
import main.java.PaymentChecksHandler.ChecksHandler.models.PaymentRequest;
import main.java.PaymentChecksHandler.ChecksHandler.service.Validator;
import main.java.PaymentChecksHandler.ChecksHandler.util.LogMessage;

import io.micrometer.core.instrument.util.StringUtils;

@Component
public class AmountValidator implements Validator {

    private static final Logger LOGGER = LogManager.getLogger(AmountVal.class);
    private static final String AMOUNT_REGEX = "[0-9]{1,7}+([.][0-9]{2})";

    @Override
    public void performValidation(PaymentRequest paymentRequest) {
        // Extract amount for readability
        String amount = paymentRequest.getPayment().getAmount();

        // Logging the validation step
        LogMessage.log(LOGGER, "Validating amount for payment request: " + paymentRequest);

        // Validation logic
        if (isInvalidAmount(amount)) {
            // Log specific failure reason
            LogMessage.log(LOGGER, "Amount is missing or invalid: " + amount);
            throw createValidationException();
        }
    }

    // Helper method to check if the amount is invalid
    private boolean isInvalidAmount(String amount) {
        return StringUtils.isBlank(amount) || !amount.matches(AMOUNT_REGEX);
    }

    // Helper method to create the validation exception
    private ValidationException createValidationException() {
        return new ValidationException(
                HttpStatus.BAD_REQUEST,
                ErrorCodeEnum.AMOUNT_VALIDATION_FAILED.getErrorCode(),
                ErrorCodeEnum.AMOUNT_VALIDATION_FAILED.getErrorMessage());
    }
}
