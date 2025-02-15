package com.example.BillingService.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.BillingService.constants.Apicalls;

@RestController
@RequestMapping(Endpoints.PAYMENTS)
public class PaymentProcessingController {

    private static final Logger LOG = LogManager.getLogger(PaymentProcessingController.class);

    private final PaymentHandler paymentHandler;
    private final PaymentStatusHandler paymentStatusHandler;
    private final TransactionConverter transactionConverter;

    @Autowired
    public PaymentProcessingController(PaymentHandler paymentHandler, PaymentStatusHandler paymentStatusHandler,
            TransactionConverter transactionConverter) {
        this.paymentHandler = paymentHandler;
        this.paymentStatusHandler = paymentStatusHandler;
        this.transactionConverter = transactionConverter;
    }

    @PostMapping(Endpoints.UPDATE_STATUS)
    public ResponseEntity<TransactionRequestResponse> handlePaymentStatusUpdate(
            @RequestBody TransactionRequestResponse transactionRequest) {
        LogMessage.setLogMessagePrefix("/UPDATE_STATUS");
        LogMessage.log(LOG, "Received payment status update request: " + transactionRequest);

        Transaction transaction = transactionConverter.convertToTransaction(transactionRequest);
        Transaction updatedTransaction = paymentStatusHandler.updateStatus(transaction);
        return ResponseEntity.ok(transactionConverter.convertToResponse(updatedTransaction));
    }

    @PostMapping(Endpoints.HANDLE_PAYMENT)
    public ResponseEntity<PaymentResult> handlePaymentRequest(
            @RequestBody PaymentProcessingRequest paymentProcessingRequest) {
        LogMessage.setLogMessagePrefix("/HANDLE_PAYMENT");
        LogMessage.log(LOG, "Handling payment processing request: " + paymentProcessingRequest);

        PaymentResult result = paymentHandler.handlePayment(paymentProcessingRequest);
        return ResponseEntity.ok(result);
    }
}