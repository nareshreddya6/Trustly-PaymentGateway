package com.cpt.payments.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cpt.payments.constants.ControllerEndpoints;
import com.cpt.payments.pojo.request.CoreTrustlyProvider;
import com.cpt.payments.pojo.response.TrustlyCoreResponse;
import com.cpt.payments.service.PaymentService;
import com.cpt.payments.util.LogMessage;

/**
 * Controller handling Trustly payment operations including initiation and status updates.
 */
@RestController
@RequestMapping(ControllerEndpoints.PAYMENT_BASE_URI)
public class PaymentController {
    
    private static final Logger log = LogManager.getLogger(PaymentController.class);
    
    // Payment status constants
    private static final String PAYMENT_SUCCESS = "SUCCESS";
    private static final String PAYMENT_FAILED = "FAILED";
    
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Initiates a new payment transaction through Trustly.
     */
    @PostMapping(ControllerEndpoints.PROCESS_PAYMENT)
    public ResponseEntity<TrustlyCoreResponse> startPayment(@RequestBody CoreTrustlyProvider request) {
        log.info("Starting new Trustly payment process: {}", request);
        
        TrustlyCoreResponse response = paymentService.initiatePayment(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Handles successful payment callback from Trustly.
     */
    @PostMapping(ControllerEndpoints.SUCCESS_PAYMENT)
    public ResponseEntity<Void> handlePaymentSuccess(@PathVariable("paymentId") String paymentId) {
        log.info("Processing successful payment callback for ID: {}", paymentId);
        
        paymentService.processPayment(paymentId, PAYMENT_SUCCESS);
        return ResponseEntity.ok().build();
    }

    /**
     * Handles failed payment callback from Trustly.
     */
    @PostMapping(ControllerEndpoints.FAIL_PAYMENT)
    public ResponseEntity<Void> handlePaymentFailure(@PathVariable("paymentId") String paymentId) {
        log.info("Processing failed payment callback for ID: {}", paymentId);
        
        paymentService.processPayment(paymentId, PAYMENT_FAILED);
        return ResponseEntity.ok().build();
    }
}