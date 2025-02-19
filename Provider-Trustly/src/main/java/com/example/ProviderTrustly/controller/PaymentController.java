package com.example.ProviderTrustly.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ProviderTrustly.constants.ControllerEndpoints;
import com.example.ProviderTrustly.pojo.request.TrustlyProviderRequest;
import com.example.ProviderTrustly.pojo.response.TrustlyProviderResponse;
import com.example.ProviderTrustly.service.PaymentService;
import com.example.ProviderTrustly.util.LogMessage;

/**
 * REST controller handling Trustly payment operations.
 * Provides endpoints for payment processing and transaction management.
 */
@RestController
@RequestMapping(ControllerEndpoints.PAYMENT_BASE_URI)
public class PaymentController {
    
    private final Logger log = LogManager.getLogger(getClass());
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Handles payment initiation requests through Trustly's payment gateway.
     * 
     * @param request Payment details and transaction information
     * @return Response containing payment status and transaction details
     */
    @PostMapping(ControllerEndpoints.PROCESS_PAYMENT)
    public ResponseEntity<TrustlyProviderResponse> handlePaymentRequest(@RequestBody TrustlyProviderRequest request) {
        String operation = ControllerEndpoints.PROCESS_PAYMENT;
        LogMessage.setLogMessagePrefix(operation);
        
        log.info("Received new payment request - Processing transaction with details: {}", request);
        
        TrustlyProviderResponse response = paymentService.initiatePayment(request);
        log.info("Payment processing completed successfully");
        
        return ResponseEntity.ok(response);
    }
}