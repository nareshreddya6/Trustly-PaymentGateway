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
public class PaymentTypeVal implements Validator {

    private static final Logger LOGGER = LogManager.getLogger(PaymentTypeValidator.class);

    private static final String SALE = "SALE";

    @Override
    public void performValidation(PaymentRequest paymentRequest) {
        LogMessage.log(LOGGER, "Validating PAYMENT TYPE request for -> " + paymentRequest);

        // Validate paymentType field
        String paymentType = paymentRequest.getPayment().getPaymentType();

        if (isBlankOrInvalidMethod(paymentType)) {
            logAndThrowValidationException("Payment type field is missing or invalid");
        }

        // Validate against PaymentTypeEnum
        if (PaymentTypeEnum.getPaymentType(paymentType) == null) {
            logAndThrowValidationException("Payment type field is not valid");
        }
    }

    // Helper method to check for blank or invalid paymentType
    private boolean isBlankOrInvalidMethod(String paymentType) {
        return paymentType == null || paymentType.trim().isEmpty() || !paymentType.equalsIgnoreCase(SALE);
    }

    // Helper method for logging and throwing validation exceptions
    private void logAndThrowValidationException(String message) {
        LogMessage.log(LOGGER, message);
        throw new ValidationException(HttpStatus.BAD_REQUEST,
                ErrorCodeEnum.PAYMENT_TYPE_VALIDATION_FAILED.getErrorCode(),
                ErrorCodeEnum.PAYMENT_TYPE_VALIDATION_FAILED.getErrorMessage());
    }
}
