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
public class MerchantTransactionIDVal implements Validator {

    private static final Logger LOGGER = LogManager.getLogger(MerchantTransactionIDValidator.class);
    private static final String MERCHANT_TXN_CODE_PATTERN = "^[A-Za-z0-9\-_]+$";

    @Override
    public void performValidation(PaymentRequest paymentRequest) {
        LogMessage.log(LOGGER, "Validating merchantTxnReference request for: " + paymentRequest);

        String merchantTxnReference = paymentRequest.getPayment().getMerchantTransactionReference();
        if (merchantTxnReference == null || merchantTxnReference.trim().isEmpty()
                || !merchantTxnReference.matches(MERCHANT_TXN_CODE_PATTERN)) {
            LogMessage.log(LOGGER, "MerchantTxnReference field is missing or not valid");
            throw new ValidationException(
                    HttpStatus.BAD_REQUEST,
                    ErrorCodeEnum.MERCHANT_TRANSACTION_REFERENCE_VALIDATION_FAILED.getErrorCode(),
                    ErrorCodeEnum.MERCHANT_TRANSACTION_REFERENCE_VALIDATION_FAILED.getErrorMessage());
        }
    }
}
