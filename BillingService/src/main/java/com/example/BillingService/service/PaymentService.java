package com.example.BillingService.service;

import com.example.BillingService.pojo.PaymentResponse;
import com.example.BillingService.pojo.ProcessingServiceRequest;

public interface PaymentService {

	PaymentResponse processPayment(ProcessingServiceRequest processingServiceRequest);
}
