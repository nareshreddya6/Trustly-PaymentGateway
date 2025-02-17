package com.example.BillingService.service;

import com.example.BillingService.dto.Transaction;

public interface PaymentStatusService {

	Transaction updatePaymentStatus(Transaction transaction);

}
