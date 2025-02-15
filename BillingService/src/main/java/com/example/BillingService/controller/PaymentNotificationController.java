package com.example.BillingService.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.BillingService.constants.ApiPaths;
import com.example.BillingService.util.LogMessage;

@RestController
@RequestMapping(ApiPaths.PAYMENT_NOTIFICATIONS)
public class PaymentNotificationController {

    private static final Logger logger = LogManager.getLogger(PaymentNotificationController.class);
    private final PaymentNotificationProcessor notificationProcessor;

    public PaymentNotificationController(PaymentNotificationProcessor notificationProcessor) {
        this.notificationProcessor = notificationProcessor;
    }

    @PostMapping
    public ResponseEntity<Void> processNotification(@RequestBody PaymentNotificationRequest request) {
        LogMessage.setLogMessagePrefix("PAYMENT_NOTIFICATION");
        logger.info("Processing payment notification: {}", request);

        notificationProcessor.handlePaymentNotification(request);
        return ResponseEntity.ok().build();
    }
}