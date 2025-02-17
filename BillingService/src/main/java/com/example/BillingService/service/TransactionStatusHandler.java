package com.example.BillingService.service;

import com.example.BillingService.dto.Transaction;

public abstract class TransactionStatusHandler {

    public abstract boolean updateStatus(Transaction transaction);
}