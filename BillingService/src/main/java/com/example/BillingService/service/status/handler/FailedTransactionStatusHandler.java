package com.example.BillingService.service.status.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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

@Component
public class FailedTransactionStatusHandler extends TransactionStatusHandler {

	private static final Logger LOGGER = LogManager.getLogger(FailedTransactionStatusHandler.class);

	@Autowired
	private TransactionDetailsDao transactionDetailsDao;

	@Autowired
	private TransactionDao transactionDao;

	@Autowired
	private TransactionLogDao transactionLogDao;

	@Override
	public boolean updateStatus(Transaction transaction) {
		LogMessage.log(LOGGER, " transaction failed -> " + transaction);
		TransactionDetails transactionDetails = transactionDetailsDao
				.getTransactionDetailsById(TransactionDetailsEnum.FAILED.getCode());
		transaction.setTxnDetailsId(transactionDetails.getId());
		transaction.setTxnStatusId(TransactionStatusEnum.FAILED.getId());
		boolean transactionStatus = transactionDao.updateTransaction(transaction);
		if (!transactionStatus) {
			LogMessage.log(LOGGER, " updating transaction failed -> " + transaction);
			return false;
		}
		TransactionLog transactionLog = TransactionLog.builder().transactionId(transaction.getId())
				.txnFromStatus(TransactionStatusEnum.PENDING.getName())
				.txnToStatus(TransactionStatusEnum.FAILED.getName()).build();
		transactionLogDao.createTransactionLog(transactionLog);
		return true;
	}

}
