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
public class CreatedTransactionStatusHandler extends TransactionStatusHandler {
	private static final Logger LOGGER = LogManager.getLogger(CreatedTransactionStatusHandler.class);

	@Autowired
	private TransactionDetailsDao transactionDetailsDao;

	@Autowired
	private TransactionDao transactionDao;

	@Autowired
	private TransactionLogDao transactionLogDao;

	@Override
	public boolean updateStatus(Transaction transaction) {
		LogMessage.log(LOGGER, " creating new transaction -> " + transaction);
		TransactionDetails transactionDetails = transactionDetailsDao
				.getTransactionDetailsById(TransactionDetailsEnum.CREATED.getCode());
		transaction.setTxnDetailsId(transactionDetails.getId());
		transaction = transactionDao.createTransaction(transaction);
		if (null == transaction) {
			LogMessage.log(LOGGER, " creating new transaction failed -> " + transaction);
			return false;
		}
		TransactionLog transactionLog = TransactionLog.builder().transactionId(transaction.getId()).txnFromStatus("-")
				.txnToStatus(TransactionStatusEnum.CREATED.getName()).build();
		transactionLogDao.createTransactionLog(transactionLog);

		return true;
	}

}
