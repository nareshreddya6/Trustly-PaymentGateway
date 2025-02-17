package com.example.BillingService.service.Impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.BillingService.constants.ErrorCodeEnum;
import com.example.BillingService.dao.TransactionDao;
import com.example.BillingService.dto.Transaction;
import com.example.BillingService.exception.PaymentProcessingException;
import com.example.BillingService.models.PaymentResponse;
import com.example.BillingService.models.ProcessingServiceRequest;
import com.example.BillingService.service.PaymentService;
import com.example.BillingService.service.ProviderHandler;
import com.example.BillingService.service.factory.ProviderHandlerFactory;
import com.example.BillingService.util.LogMessage;

/**
 * Implementation of the payment service that handles payment processing
 * through various payment providers.
 */
@Component
public class PaymentServiceImpl implements PaymentService {
    
    private final Logger logger = LogManager.getLogger(getClass());
    
    private final TransactionDao transactionDao;
    private final ProviderHandlerFactory providerFactory;
    
    @Autowired
    public PaymentServiceImpl(TransactionDao transactionDao, ProviderHandlerFactory providerFactory) {
        this.transactionDao = transactionDao;
        this.providerFactory = providerFactory;
    }
    
    @Override
    public PaymentResponse processPayment(ProcessingServiceRequest paymentRequest) {
        // Fetch and validate transaction
        Transaction paymentTransaction = fetchAndValidateTransaction(paymentRequest.getTransactionId());
        
        // Get appropriate payment provider
        ProviderHandler paymentProvider = getPaymentProvider(paymentTransaction.getProviderId());
        
        // Process the payment and return response
        PaymentResponse response = paymentProvider.processPayment(paymentTransaction, paymentRequest);
        LogMessage.log(logger, "Payment processed successfully. Response: " + response);
        
        return response;
    }
    
    private Transaction fetchAndValidateTransaction(String transactionId) {
        Transaction transaction = transactionDao.getTransactionById(transactionId);
        LogMessage.log(logger, "Retrieved transaction details: " + transaction);
        
        if (transaction == null) {
            LogMessage.log(logger, "No transaction found with ID: " + transactionId);
            throw new PaymentProcessingException(
                HttpStatus.BAD_REQUEST,
                ErrorCodeEnum.PAYMENT_NOT_FOUND.getErrorCode(),
                ErrorCodeEnum.PAYMENT_NOT_FOUND.getErrorMessage()
            );
        }
        
        return transaction;
    }
    
    private ProviderHandler getPaymentProvider(String providerId) {
        ProviderHandler provider = providerFactory.getProviderHandler(providerId);
        LogMessage.log(logger, "Selected payment provider: " + provider);
        
        if (provider == null) {
            LogMessage.log(logger, "No payment provider found for ID: " + providerId);
            throw new PaymentProcessingException(
                HttpStatus.BAD_REQUEST,
                ErrorCodeEnum.PROVIDER_NOT_FOUND.getErrorCode(),
                ErrorCodeEnum.PROVIDER_NOT_FOUND.getErrorMessage()
            );
        }
        
        return provider;
    }
}