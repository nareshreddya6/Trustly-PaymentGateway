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
public class PhoneNumberVal implements Validator {

    private static final Logger LOGGER = LogManager.getLogger(PhoneNumberValidator.class);

    private static final String PHONE_PATTERN = "^[+]{1}[0-9]{1,15}$";

    @Override
    public void performValidation(PaymentRequest paymentRequest) {
        LogMessage.log(LOGGER, "Validating phone number request for -> " + paymentRequest);

        String phoneNumber = paymentRequest.getUser().getPhoneNumber();

        if (isInvalidPhoneNumber(phoneNumber)) {
            LogMessage.log(LOGGER, "Phone number field is missing or not valid.");
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    ErrorCodeEnum.PHONE_NUMBER_VALIDATION_FAILED.getErrorCode(),
                    ErrorCodeEnum.PHONE_NUMBER_VALIDATION_FAILED.getErrorMessage());
        }
    }

    // Helper method to check if phone number is invalid
    private boolean isInvalidPhoneNumber(String phoneNumber) {
        return phoneNumber == null || phoneNumber.trim().isEmpty() || !phoneNumber.matches(PHONE_PATTERN);
    }
}
