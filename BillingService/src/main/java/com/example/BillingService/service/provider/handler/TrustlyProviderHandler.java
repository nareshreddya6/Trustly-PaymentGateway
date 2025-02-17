package com.example.BillingService.service.provider.handler;

import com.example.BillingService.constants.ErrorCodeEnum;
import com.example.BillingService.constants.TransactionStatusEnum;
import com.example.BillingService.dao.TransactionDao;
import com.example.BillingService.dto.Transaction;
import com.example.BillingService.exception.PaymentProcessingException;
import com.example.BillingService.http.HttpRequest;
import com.example.BillingService.http.HttpRestTemplateEngine;
import com.example.BillingService.models.PaymentResponse;
import com.example.BillingService.models.ProcessingServiceRequest;
import com.example.BillingService.models.provider.request.TrustlyProviderRequest;
import com.example.BillingService.models.provider.response.TrustlyErrorResponse;
import com.example.BillingService.models.provider.response.TrustlyProviderResponse;
import com.example.BillingService.service.PaymentStatusService;
import com.example.BillingService.service.ProviderHandler;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TrustlyProviderHandler implements ProviderHandler {
    private static final Logger logger = LogManager.getLogger(TrustlyProviderHandler.class);
    private final Gson jsonMapper = new Gson();

    @Value("${trustly.provider.service.process.payment}")
    private String paymentApiEndpoint;

    @Autowired
    private HttpRestTemplateEngine httpEngine;

    @Autowired
    private PaymentStatusService statusService;

    @Autowired
    private TransactionDao txnRepository;

    @Override
    public PaymentResponse processPayment(Transaction transaction, ProcessingServiceRequest customerData) {
        logger.info("Starting payment processing via Trustly gateway");
        
        try {
            // Create payment request with customer and transaction details
            var trustlyRequest = createPaymentRequest(transaction, customerData);
            logger.debug("Created payment request: {}", trustlyRequest);

            // Mark as in-progress
            markTransactionAsPending(transaction);

            // Process payment through Trustly API
            var apiResponse = submitPaymentRequest(trustlyRequest);
            ensureValidResponse(apiResponse);

            // Handle successful payment flow
            return finalizeSuccessfulPayment(transaction, apiResponse);
            
        } catch (Exception e) {
            logger.error("Payment processing failed: {}", e.getMessage());
            throw e;
        }
    }

    private TrustlyProviderRequest createPaymentRequest(Transaction txn, ProcessingServiceRequest customer) {
        return TrustlyProviderRequest.builder()
            .amount(txn.getAmount())
            .currency(txn.getCurrency())
            .creditorNumber(txn.getCreditorAccount())
            .debitorNumber(txn.getDebitorAccount())
            .transactionReference(txn.getTxnReference())
            .firstName(customer.getFirstName())
            .lastName(customer.getLastName())
            .email(customer.getEmail())
            .build();
    }

    private void markTransactionAsPending(Transaction txn) {
        logger.info("Updating transaction {} status to PENDING", txn.getTxnReference());
        txn.setTxnStatusId(TransactionStatusEnum.PENDING.getId());
        statusService.updatePaymentStatus(txn);
    }

    private ResponseEntity<String> submitPaymentRequest(TrustlyProviderRequest paymentData) {
        var apiRequest = HttpRequest.builder()
            .httpMethod(HttpMethod.POST)
            .url(paymentApiEndpoint)
            .request(jsonMapper.toJson(paymentData))
            .build();

        return httpEngine.execute(apiRequest);
    }

    private void ensureValidResponse(ResponseEntity<String> response) {
        if (response == null || response.getBody() == null) {
            logger.error("No response received from payment gateway");
            throw new PaymentProcessingException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCodeEnum.FAILED_TO_CREATE_TRANSACTION.getErrorCode(),
                "Failed to receive response from payment gateway"
            );
        }

        if (!response.getStatusCode().is2xxSuccessful()) {
            handleFailedPayment(response);
        }
    }

    private void handleFailedPayment(ResponseEntity<String> response) {
        var errorDetails = jsonMapper.fromJson(response.getBody(), TrustlyErrorResponse.class);
        logger.error("Payment gateway error: {}", errorDetails);

        if (errorDetails.isTpProviderError()) {
            throw new PaymentProcessingException(
                HttpStatus.BAD_GATEWAY,
                ErrorCodeEnum.TP_TRUSTLY_ERROR.getErrorCode(),
                "Trustly gateway processing error"
            );
        }

        throw new PaymentProcessingException(
            HttpStatus.BAD_GATEWAY,
            errorDetails.getErrorCode(),
            errorDetails.getErrorMessage()
        );
    }

    private PaymentResponse finalizeSuccessfulPayment(Transaction txn, ResponseEntity<String> response) {
        var paymentResult = jsonMapper.fromJson(
            response.getBody(),
            TrustlyProviderResponse.class
        );

        // Store payment gateway reference
        txn.setProviderReference(paymentResult.getPaymentId());
        txnRepository.updateProviderReference(txn);

        var successResponse = PaymentResponse.builder()
            .paymentReference(txn.getTxnReference())
            .redirectUrl(paymentResult.getRedirectUrl())
            .build();

        logger.info("Successfully processed payment {}", txn.getTxnReference());
        return successResponse;
    }
}