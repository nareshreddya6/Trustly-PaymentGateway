package com.example.ProviderTrustly.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.ProviderTrustly.constants.ErrorCodeEnum;
import com.example.ProviderTrustly.exception.PaymentProcessException;
import com.example.ProviderTrustly.http.HttpRequest;
import com.example.ProviderTrustly.http.HttpRestTemplateEngine;
import com.example.ProviderTrustly.pojo.request.TrustlyProviderRequest;
import com.example.ProviderTrustly.pojo.response.TrustlyProviderResponse;
import com.example.ProviderTrustly.service.PaymentService;
import com.example.ProviderTrustly.service.formatter.request.InitiatePaymentRequestHandler;
import com.example.ProviderTrustly.service.formatter.response.InitiatePaymentResponseHandler;
import com.example.ProviderTrustly.service.helper.PaymentServiceHelper;
import com.example.ProviderTrustly.util.LogMessage;

/**
 * Implementation of the payment service for processing Trustly payments.
 * Handles payment initiation and communication with Trustly's payment gateway.
 */
@Service
public class PaymentServiceImpl implements PaymentService {
    
    private final Logger log = LogManager.getLogger(getClass());
    
    private final InitiatePaymentRequestHandler requestFormatter;
    private final InitiatePaymentResponseHandler responseFormatter;
    private final HttpRestTemplateEngine httpClient;
    private final PaymentServiceHelper storageHelper;

    @Autowired
    public PaymentServiceImpl(
            InitiatePaymentRequestHandler requestFormatter,
            InitiatePaymentResponseHandler responseFormatter,
            HttpRestTemplateEngine httpClient,
            PaymentServiceHelper storageHelper) {
        this.requestFormatter = requestFormatter;
        this.responseFormatter = responseFormatter;
        this.httpClient = httpClient;
        this.storageHelper = storageHelper;
    }

    @Override
    public TrustlyProviderResponse initiatePayment(TrustlyProviderRequest paymentRequest) {
        // Format the request for Trustly API
        HttpRequest formattedRequest = requestFormatter.prepareRequest(paymentRequest);
        
        // Send request to Trustly and get response
        ResponseEntity<String> trustlyResponse = sendRequestToTrustly(formattedRequest);
        
        // Store transaction details
        String transactionRef = paymentRequest.getTransactionReference();
        storageHelper.saveProviderRequestResponse(transactionRef, formattedRequest, trustlyResponse);
        
        // Process and return the response
        return responseFormatter.processResponse(trustlyResponse);
    }
    
    private ResponseEntity<String> sendRequestToTrustly(HttpRequest request) {
        ResponseEntity<String> response = httpClient.execute(request);
        
        if (response == null) {
            log.error("Failed to establish connection with Trustly payment gateway");
            throw new PaymentProcessException(
                HttpStatus.BAD_REQUEST,
                ErrorCodeEnum.FAILED_TO_CONNECT_TO_TRUSTLY.getErrorCode(),
                ErrorCodeEnum.FAILED_TO_CONNECT_TO_TRUSTLY.getErrorMessage()
            );
        }
        
        return response;
    }
}