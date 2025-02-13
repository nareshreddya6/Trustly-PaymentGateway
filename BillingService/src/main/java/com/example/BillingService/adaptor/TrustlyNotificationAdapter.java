package com.cpt.payments.adapter;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.BillingService.constants.ErrorCodeEnum;
import com.example.BillingService.constants.TransactionStatusEnum;


import com.google.gson.Gson;

@Component
public class TrustlyNotificationAdapter {

    private static final String FAILED = "Failed";
    private static final String SUCCESS = "Success";
    private static final Logger LOGGER = LogManager.getLogger(TrustlyNotificationAdapter.class);

    @Autowired
    private PaymentStatusService paymentStatusService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private SHA256RSASignatureVerifier signatureVerifier;

    @Autowired
    private TransactionDao transactionDao;

    private static final String PUBLIC_KEY_PATH = "./src/main/java/com/cpt/payments/util/public_trustly.pem";

    public void processNotification(TrustlyNotificationRequest request) {
        LogMessage.log(LOGGER, "Validating signature for request: " + request);
        String signature = httpServletRequest.getHeader("signature");

        if (signature == null) {
            LogMessage.log(LOGGER, "Signature not found in request headers");
            throw new PaymentProcessingException(HttpStatus.UNAUTHORIZED, ErrorCodeEnum.SIGNATURE_NOT_FOUND);
        }

        Gson gson = new Gson();
        try {
            if (!signatureVerifier.verifySignature(signature, gson.toJson(request), PUBLIC_KEY_PATH)) {
                LogMessage.log(LOGGER, "Invalid signature detected");
                throw new PaymentProcessingException(HttpStatus.BAD_REQUEST, ErrorCodeEnum.INVALID_SIGNATURE);
            }
        } catch (Exception e) {
            LogMessage.log(LOGGER, "Exception during signature validation: " + e.getMessage());
            throw new PaymentProcessingException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodeEnum.SIGNATURE_VERIFICATION_FAILED);
        }

        LogMessage.log(LOGGER, "Signature verified for request: " + request);

        Transaction transaction = transactionDao.getTransactionByProviderReference(request.getPaymentId());
        if (transaction == null) {
            LogMessage.log(LOGGER, "Transaction not found for payment ID: " + request.getPaymentId());
            throw new PaymentProcessingException(HttpStatus.BAD_REQUEST, ErrorCodeEnum.PAYMENT_NOT_FOUND);
        }

        updateTransactionStatus(request, transaction);
        transaction = paymentStatusService.updatePaymentStatus(transaction);
        updateProviderDetails(transaction, request);
        transactionDao.updateProviderCodeAndMessage(transaction);
    }

    private void updateTransactionStatus(TrustlyNotificationRequest request, Transaction transaction) {
        if (SUCCESS.equalsIgnoreCase(request.getStatus())) {
            transaction.setTxnStatusId(TransactionStatusEnum.APPROVED.getId());
        } else if (FAILED.equalsIgnoreCase(request.getStatus())) {
            transaction.setTxnStatusId(TransactionStatusEnum.FAILED.getId());
        }
    }

    private void updateProviderDetails(Transaction transaction, TrustlyNotificationRequest request) {
        transaction.setProviderCode(request.getCode());
        transaction.setProviderMessage(request.getMessage());
    }
}