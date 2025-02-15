package com.example.BillingService.dao;

import com.example.BillingService.dto.Transaction;

public interface TransactionDao {
    Transaction saveTransaction(Transaction transaction);

    boolean modifyTransaction(Transaction transaction);

    Transaction fetchTransactionById(long transactionId);

    void updateTransactionReference(Transaction transaction);

    Transaction fetchTransactionByReference(String reference);

    void updateTransactionStatus(Transaction transaction);
}