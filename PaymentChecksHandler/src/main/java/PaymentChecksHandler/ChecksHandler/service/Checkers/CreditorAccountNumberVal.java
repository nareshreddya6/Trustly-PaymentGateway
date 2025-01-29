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
public class CreditorAccountNumberVal implements Validator {

    private static final Logger LOGGER = LogManager.getLogger(CreditorAccountNumberValidator.class);
    private static final String ACCOUNT_NUMBER_PATTERN = "[0-9]{1,20}";

    @Override
    public void performValidation(PaymentRequest paymentRequest) {
        LogMessage.log(LOGGER, "Starting validation for Creditor Account Number: " + paymentRequest);

        String accountNumber = paymentRequest.getPayment().getDebitorAccount();

        if (isNullOrEmpty(accountNumber) || !accountNumber.matches(ACCOUNT_NUMBER_PATTERN)) {
            LogMessage.log(LOGGER, "Invalid or missing Creditor Account Number.");
            throw new ValidationException(HttpStatus.BAD_REQUEST,
                    ErrorCodeEnum.CREDITOR_ACCOUNT_NUMBER_VALIDATION_FAILED.getErrorCode(),
                    ErrorCodeEnum.CREDITOR_ACCOUNT_NUMBER_VALIDATION_FAILED.getErrorMessage());
        }
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
