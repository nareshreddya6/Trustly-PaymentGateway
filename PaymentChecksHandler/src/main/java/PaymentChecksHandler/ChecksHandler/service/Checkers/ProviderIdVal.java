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
public class ProviderIdVal implements Validator {

    private static final Logger LOGGER = LogManager.getLogger(ProviderIdValidator.class);

    private static final String TRUSTLY = "TRUSTLY";

    @Override
    public void performValidation(PaymentRequest paymentRequest) {
        LogMessage.log(LOGGER, "Validating PROVIDER ID request for -> " + paymentRequest);

        String providerId = paymentRequest.getPayment().getProviderId();

        if (isInvalidProviderId(providerId)) {
            LogMessage.log(LOGGER, "Provider ID field is missing or not valid");
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    ErrorCodeEnum.PROVIDER_ID_VALIDATION_FAILED.getErrorCode(),
                    ErrorCodeEnum.PROVIDER_ID_VALIDATION_FAILED.getErrorMessage());
        }

        // Validate providerId against ProviderEnum
        if (ProviderEnum.getProviderEnum(providerId) == null) {
            LogMessage.log(LOGGER, "Provider ID field is not valid");
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    ErrorCodeEnum.PROVIDER_ID_VALIDATION_FAILED.getErrorCode(),
                    ErrorCodeEnum.PROVIDER_ID_VALIDATION_FAILED.getErrorMessage());
        }
    }

    // Helper method to check for invalid providerId
    private boolean isInvalidProviderId(String providerId) {
        return providerId == null || providerId.trim().isEmpty() || !providerId.equalsIgnoreCase(TRUSTLY);
    }
}
