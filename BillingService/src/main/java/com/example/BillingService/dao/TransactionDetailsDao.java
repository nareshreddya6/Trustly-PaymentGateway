package com.example.BillingService.dao;

import com.example.BillingService.dto.TransactionDetails;

public interface TransactionDetailsDao {

	public TransactionDetails getTransactionDetailsById(String code);
}
