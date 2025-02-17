package com.example.BillingService.service.Impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.BillingService.constants.ErrorCodeEnum;
import com.example.BillingService.constants.TransactionStatusEnum;
import com.example.BillingService.dto.Transaction;
import com.example.BillingService.exception.PaymentProcessingException;
import com.example.BillingService.service.PaymentStatusService;
import com.example.BillingService.service.TransactionStatusHandler;
import com.example.BillingService.service.factory.TransactionStatusFactory;
import com.example.BillingService.util.LogMessage;

@Component
public class PaymentStatusServiceImpl implements PaymentStatusService {

	private static final Logger LOGGER = LogManager.getLogger(PaymentStatusServiceImpl.class);

	@Autowired
	private TransactionStatusFactory transactionStatusFactory;

	@Override
	public Transaction updatePaymentStatus(Transaction transaction) {
		LogMessage.log(LOGGER, " updating transaction -> " + transaction.getTxnStatusId());

		TransactionStatusEnum transactionStatusEnum = TransactionStatusEnum
				.getTransactionStatusEnum(transaction.getTxnStatusId());
		TransactionStatusHandler transactionStatusHandler = transactionStatusFactory
				.getStatusFactory(transactionStatusEnum);
		if (null == transactionStatusHandler) {
			LogMessage.log(LOGGER, " invalid transaction handler -> " + transaction.getTxnStatusId());
			throw new PaymentProcessingException(HttpStatus.INTERNAL_SERVER_ERROR,
					ErrorCodeEnum.TRANSACTION_STATUS_HANDLER_NOT_FOUND.getErrorCode(),
					ErrorCodeEnum.TRANSACTION_STATUS_HANDLER_NOT_FOUND.getErrorMessage());
		}
		boolean transtionStatus = transactionStatusHandler.updateStatus(transaction);
		if (!transtionStatus) {
			LogMessage.log(LOGGER, " transaction status update failed -> " + transaction.getTxnStatusId());
			throw new PaymentProcessingException(HttpStatus.INTERNAL_SERVER_ERROR,
					ErrorCodeEnum.TRANSACTION_STATUS_UPDATE_FAILED.getErrorCode(),
					ErrorCodeEnum.TRANSACTION_STATUS_UPDATE_FAILED.getErrorMessage());
		}
		return transaction;
	}

}
