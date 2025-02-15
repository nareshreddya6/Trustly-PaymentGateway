package com.example.BillingService.models;

import lombok.Data;

@Data
public class PaymentRequest {
    private String paymentReference;
    private String creditorAccount;
    private String debitorAccount;
}
