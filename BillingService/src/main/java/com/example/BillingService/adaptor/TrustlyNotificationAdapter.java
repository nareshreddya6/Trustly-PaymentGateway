package com.cpt.payments.adapter;

import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import com.example.BillingService.constants.ErrorCodeEnum;
import com.example.BillingService.constants.TransactionStatusEnum;
import com.example.BillingService.exception.PaymentProcessingException;
import com.example.BillingService.model.Transaction;
import com.example.BillingService.util.LogMessage;
import com.google.gson.Gson;

@Component
public class PaymentNotificationProcessor {
    private static final Logger LOGGER = LogManager.getLogger(PaymentNotificationProcessor.class);
    
    private final PaymentStatusService paymentStatusService;
    private final HttpServletRequest httpServletRequest;
    private final SignatureVerificationService signatureVerifier;
    private final TransactionRepository transactionRepository;
    private final Gson gson;

    @Value("${trustly.public.key.path}")
    private String publicKeyPath;

    public PaymentNotificationProcessor(
            PaymentStatusService paymentStatusService,
            HttpServletRequest httpServletRequest,
            SignatureVerificationService signatureVerifier,
            TransactionRepository transactionRepository) {
        this.paymentStatusService = paymentStatusService;
        this.httpServletRequest = httpServletRequest;
        this.signatureVerifier = signatureVerifier;
        this.transactionRepository = transactionRepository;
        this.gson = new Gson();
    }

    public void handlePaymentNotification(PaymentNotificationRequest notificationRequest) {
        validateAndVerifySignature(notificationRequest);
        processPaymentUpdate(notificationRequest);
    }

    private void validateAndVerifySignature(PaymentNotificationRequest request) {
        String signature = extractSignature();
        verifySignature(signature, request);
    }

    private String extractSignature() {
        String signature = httpServletRequest.getHeader("signature");
        if (signature == null) {
            LogMessage.log(LOGGER, "Payment notification received without signature");
            throw new PaymentProcessingException(HttpStatus.UNAUTHORIZED, ErrorCodeEnum.SIGNATURE_NOT_FOUND);
        }
        return signature;
    }

    private void verifySignature(String signature, PaymentNotificationRequest request) {
        try {
            boolean isValid = signatureVerifier.verify(signature, gson.toJson(request), publicKeyPath);
            if (!isValid) {
                LogMessage.log(LOGGER, "Invalid signature detected in payment notification");
                throw new PaymentProcessingException(HttpStatus.BAD_REQUEST, ErrorCodeEnum.INVALID_SIGNATURE);
            }
        } catch (Exception e) {
            LogMessage.log(LOGGER, "Signature verification failed: " + e.getMessage());
            throw new PaymentProcessingException(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodeEnum.SIGNATURE_VERIFICATION_FAILED);
        }
    }

    private void processPaymentUpdate(PaymentNotificationRequest request) {
        Transaction transaction = findTransaction(request.getPaymentId());
        updateTransactionStatus(transaction, request);
        transaction = paymentStatusService.updatePaymentStatus(transaction);
        updateTransactionDetails(transaction, request);
        transactionRepository.save(transaction);
    }

    private Transaction findTransaction(String paymentId) {
        return transactionRepository.findByProviderReference(paymentId)
                .orElseThrow(() -> {
                    LogMessage.log(LOGGER, "Transaction not found for payment ID: " + paymentId);
                    return new PaymentProcessingException(HttpStatus.BAD_REQUEST, ErrorCodeEnum.PAYMENT_NOT_FOUND);
                });
    }

    private void updateTransactionStatus(Transaction transaction, PaymentNotificationRequest request) {
        TransactionStatusEnum newStatus = determineTransactionStatus(request.getStatus());
        transaction.setTxnStatusId(newStatus.getId());
    }

    private TransactionStatusEnum determineTransactionStatus(String status) {
        return "Success".equalsIgnoreCase(status) 
                ? TransactionStatusEnum.APPROVED 
                : TransactionStatusEnum.FAILED;
    }

    private void updateTransactionDetails(Transaction transaction, PaymentNotificationRequest request) {
        transaction.setProviderCode(request.getCode());
        transaction.setProviderMessage(request.getMessage());
        transaction.setLastUpdatedAt(LocalDateTime.now());
    }
}