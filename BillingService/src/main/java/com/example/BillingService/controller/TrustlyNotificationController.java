package com.cpt.payments.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.example.BillingService.constants.Apicalls;


@RestController
@RequestMapping(Endpoints.TRUSTLY)
public class TrustlyNotificationController {

    private static final Logger LOGGER = LogManager.getLogger(TrustlyNotificationController.class);
    private final TrustlyNotificationAdapter trustlyNotificationAdapter;

    public TrustlyNotificationController(TrustlyNotificationAdapter trustlyNotificationAdapter) {
        this.trustlyNotificationAdapter = trustlyNotificationAdapter;
    }

    @PostMapping(Endpoints.NOTIFICATION)
    public ResponseEntity<Void> processNotification(@RequestBody TrustlyNotificationRequest request) {
        LogMessage.setLogMessagePrefix("/TRUSTLY_NOTIFICATION");
        LogMessage.log(LOGGER, "Trustly notification request: " + request);

        trustlyNotificationAdapter.processNotification(request);
        return ResponseEntity.ok().build();
    }
}
