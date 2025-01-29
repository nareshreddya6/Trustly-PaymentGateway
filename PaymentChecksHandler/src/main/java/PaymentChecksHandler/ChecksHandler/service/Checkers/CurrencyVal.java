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
public class CurrencyVal implements Validator {

    private static final Logger LOGGER = LogManager.getLogger(CurrencyValidator.class);
    private static final String CURRENCY_REGEX = "^[A-Z]{3}$";

    @Override
    public void performValidation(PaymentRequest paymentRequest) {
        LogMessage.log(LOGGER, "Validating currency for request -> " + paymentRequest);
        String currency = paymentRequest.getPayment().getCurrency();

        if (isInvalidCurrency(currency)) {
            LogMessage.log(LOGGER, "Invalid currency format: " + currency);
            throw new ValidationException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCodeEnum.CURRENCY_VALIDATION_FAILED.getErrorCode(),
                    ErrorCodeEnum.CURRENCY_VALIDATION_FAILED.getErrorMessage());
        }
    }

    private boolean isInvalidCurrency(String currency) {
        return currency == null || currency.trim().isEmpty() || !currency.matches(CURRENCY_REGEX);
    }
}
