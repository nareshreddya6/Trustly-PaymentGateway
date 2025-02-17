package com.example.BillingService.service;

import com.example.BillingService.dto.Transaction;
import com.example.BillingService.pojo.PaymentResponse;
import com.example.BillingService.pojo.ProcessingServiceRequest;

public interface ProviderHandler {

	PaymentResponse processPayment(Transaction transaction, ProcessingServiceRequest processingServiceRequest);

}
