package com.cpt.payments.service.impl.validators;

import javax.xml.bind.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import main.java.PaymentChecksHandler.ChecksHandler.Constants.TransactionErrorCodes;
import main.java.PaymentChecksHandler.ChecksHandler.exceptions.ValidationException;
import main.java.PaymentChecksHandler.ChecksHandler.models.PaymentRequest;
import main.java.PaymentChecksHandler.ChecksHandler.service.Checkers;
import main.java.PaymentChecksHandler.ChecksHandler.utils.LogMessage;
import main.java.PaymentChecksHandler.ChecksHandler.utils;

import main.java.PaymentChecksHandler.ChecksHandler.constants.PaymentSaveOutcome;
import com.cpt.payments.dao.MerchantTransactionRequestDao;
import com.cpt.payments.dto.MerchantTransactionRequest;

import com.google.gson.Gson;

@Component
public class DuplicateTransactionValidator implements Validator {

    private static final Logger LOGGER = LogManager.getLogger(DuplicateTransactionValidator.class);

    @Autowired
    private MerchantTransactionRequestDao merchanttransactionrequestDao;

    private final Gson gson = new Gson();

    @Override
    public void doValidate(PaymentRequest paymentRequest) {
        String transactionId = paymentRequest.getPayment().getMerchantTransactionReference();

        if (isDuplicateTransaction(transactionId)) {
            handleDuplicateTransaction();
        }

        MerchantTransactionRequest merchanttransactionrequest = buildMerchantTransactionRequest(transactionId,
                paymentRequest);
        LogMessage.log(LOGGER, "Generated merchant payment request: " + merchanttransactionrequest);

        saveMerchantTransactionRequest(merchanttransactionrequest);
    }

    private boolean isDuplicateTransaction(String transactionId) {
        MerchantTransactionRequest existingRequest = merchanttransactionrequestDao
                .getMerchantTransactionRequest(transactionId);
        LogMessage.log(LOGGER, "Checking for existing transaction: " + existingRequest);
        return existingRequest != null;
    }

    private void handleDuplicateTransaction() {
        LogMessage.log(LOGGER, "Duplicate transaction detected.");
        throw new ValidationException(
                HttpStatus.BAD_REQUEST,
                ErrorCodeEnum.DUPLICATE_TRANSACTION.getErrorCode(),
                ErrorCodeEnum.DUPLICATE_TRANSACTION.getErrorMessage());
    }

    private MerchantTransactionRequest buildMerchantTransactionRequest(String transactionId,
            PaymentRequest paymentRequest) {
        return MerchantTransactionRequest.builder()
                .merchantTransactionReference(transactionId)
                .transactionRequest(gson.toJson(paymentRequest))
                .build();
    }

    private void saveMerchantTransactionRequest(MerchantTransactionRequest merchanttransactionrequest) {
        PaymentSaveOutcome result = merchanttransactionrequestDao
                .insertMerchantTransactionRequest(merchanttransactionrequest);

        switch (result) {
            case IS_DUPLICATE:
                LogMessage.log(LOGGER, "Duplicate transaction detected during insert.");
                throw new ValidationException(
                        HttpStatus.BAD_REQUEST,
                        ErrorCodeEnum.DUPLICATE_TRANSACTION.getErrorCode(),
                        ErrorCodeEnum.DUPLICATE_TRANSACTION.getErrorMessage());

            case IS_ERROR:
                LogMessage.log(LOGGER, "Error occurred while saving transaction request.");
                throw new ValidationException(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ErrorCodeEnum.GENERIC_EXCEPTION.getErrorCode(),
                        ErrorCodeEnum.GENERIC_EXCEPTION.getErrorMessage());

            default:
                LogMessage.log(LOGGER, "Transaction successfully saved.");
        }
    }
}
