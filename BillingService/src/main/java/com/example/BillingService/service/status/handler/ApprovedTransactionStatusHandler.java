package com.example.BillingService.service.status.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.BillingService.constants.TransactionDetailsEnum;
import com.example.BillingService.constants.TransactionStatusEnum;
import com.example.BillingService.dao.TransactionDao;
import com.example.BillingService.dao.TransactionDetailsDao;
import com.example.BillingService.dao.TransactionLogDao;
import com.example.BillingService.dto.Transaction;
import com.example.BillingService.dto.TransactionDetails;
import com.example.BillingService.dto.TransactionLog;
import com.example.BillingService.service.TransactionStatusHandler;
import com.example.BillingService.util.LogMessage;

import lombok.RequiredArgsConstructor;

/**
 * Handles the status update logic for approved transactions.
 * Updates transaction details and maintains transaction logs.
 */
@Component
@RequiredArgsConstructor
public class ApprovedTransactionStatusHandler extends TransactionStatusHandler {

    private static final Logger logger = LoggerFactory.getLogger(ApprovedTransactionStatusHandler.class);
    
    private final TransactionDetailsDao transactionDetailsDao;
    private final TransactionDao transactionDao;
    private final TransactionLogDao transactionLogDao;

    @Override
    public boolean updateStatus(Transaction transaction) {
        if (transaction == null) {
            logger.error("Cannot process null transaction");
            return false;
        }

        logger.info("Processing approved transaction: {}", transaction.getId());
        
        try {
            // Fetch and set approved transaction details
            TransactionDetails approvedDetails = fetchApprovedTransactionDetails();
            updateTransactionWithApprovedStatus(transaction, approvedDetails);
            
            // Update transaction in database
            if (!updateTransactionInDatabase(transaction)) {
                return false;
            }
            
            // Log the status change
            logStatusTransition(transaction);
            
            logger.info("Successfully processed approved transaction: {}", transaction.getId());
            return true;
            
        } catch (Exception e) {
            logger.error("Failed to process approved transaction: {}", transaction.getId(), e);
            return false;
        }
    }
    
    private TransactionDetails fetchApprovedTransactionDetails() {
        return transactionDetailsDao.getTransactionDetailsById(
            TransactionDetailsEnum.APPROVED.getCode()
        );
    }
    
    private void updateTransactionWithApprovedStatus(Transaction transaction, TransactionDetails details) {
        transaction.setTxnDetailsId(details.getId());
        transaction.setTxnStatusId(TransactionStatusEnum.APPROVED.getId());
    }
    
    private boolean updateTransactionInDatabase(Transaction transaction) {
        boolean updated = transactionDao.updateTransaction(transaction);
        if (!updated) {
            logger.error("Failed to update transaction in database: {}", transaction.getId());
        }
        return updated;
    }
    
    private void logStatusTransition(Transaction transaction) {
        TransactionLog statusLog = TransactionLog.builder()
            .transactionId(transaction.getId())
            .txnFromStatus(TransactionStatusEnum.PENDING.getName())
            .txnToStatus(TransactionStatusEnum.APPROVED.getName())
            .build();
            
        transactionLogDao.createTransactionLog(statusLog);
    }
}