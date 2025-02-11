package com.cpt.payments.controller;

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
public class PaymentController {

    private static final Logger LOGGER = LogManager.getLogger(PaymentController.class);

    private final PaymentService paymentService;
    private final PaymentStatusService paymentStatusService;
    private final TransactionMapper transactionMapper;

    public PaymentController(PaymentService paymentService, PaymentStatusService paymentStatusService, TransactionMapper transactionMapper) {
        this.paymentService = paymentService;
        this.paymentStatusService = paymentStatusService;
        this.transactionMapper = transactionMapper;
    }

    @PostMapping(Endpoints.STATUS_UPDATE)
    public ResponseEntity<TransactionReqRes> processPaymentStatus(@RequestBody TransactionReqRes transactionReqRes) {
        LogMessage.setLogMessagePrefix("/STATUS_UPDATE");
        LogMessage.log(LOGGER, "Payment request: " + transactionReqRes);

        Transaction transaction = transactionMapper.toDTO(transactionReqRes);
        Transaction response = paymentStatusService.updatePaymentStatus(transaction);
        return ResponseEntity.ok(transactionMapper.toResponseObject(response));
    }

    @PostMapping(Endpoints.PROCESS_PAYMENT)
    public ResponseEntity<PaymentResponse> processPayment(@RequestBody ProcessingServiceRequest processingServiceRequest) {
        LogMessage.setLogMessagePrefix("/PROCESS_PAYMENT");
        LogMessage.log(LOGGER, "Processing service request: " + processingServiceRequest);

        PaymentResponse response = paymentService.processPayment(processingServiceRequest);
        return ResponseEntity.ok(response);
    }
}
