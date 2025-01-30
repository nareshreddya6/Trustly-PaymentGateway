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
public class DebitorAccountNumberVal implements Validator {

    private static final Logger LOGGER = LogManager.getLogger(DebitorAccountNumberValidator.class);
    private static final String ACCOUNT_NUMBER_PATTERN = "^[0-9]{1,20}";

    @Override
    public void performValidation(PaymentRequest paymentRequest) {
        LogMessage.log(LOGGER, "Validating debitor account for request -> " + paymentRequest);

        String debitorAccount = paymentRequest.getPayment().getDebitorAccount();

        if (isInvalidDebitorAccount(debitorAccount)) {
            LogMessage.log(LOGGER, "Debitor account number is invalid or missing: " + debitorAccount);
            throw new ValidationException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCodeEnum.DEBITOR_ACCOUNT_NUMBER_VALIDATION_FAILED.getErrorCode(),
                    ErrorCodeEnum.DEBITOR_ACCOUNT_NUMBER_VALIDATION_FAILED.getErrorMessage());
        }
    }

    private boolean isInvalidDebitorAccount(String account) {
        return account == null || account.trim().isEmpty() || !account.matches(ACCOUNT_NUMBER_PATTERN);
    }
}
