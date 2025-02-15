package com.example.BillingService.dao.Impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.BillingService.dao.TransactionDao;
import com.example.BillingService.dto.Transaction;
import com.example.BillingService.util.LogHelper;

@Repository
public class TransactionImpl implements TransactionDao {
    private static final Logger logger = LogManager.getLogger(TransactionImpl.class);

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public Transaction saveTransaction(Transaction transaction) {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(buildInsertQuery(), new BeanPropertySqlParameterSource(transaction), keyHolder);
            transaction.setId(keyHolder.getKey().intValue());
        } catch (Exception e) {
            LogHelper.error(logger, "Error saving transaction: {}", e.getMessage());
        }
        return transaction;
    }

    @Override
    public boolean modifyTransaction(Transaction transaction) {
        try {
            jdbcTemplate.update(buildUpdateQuery(), new BeanPropertySqlParameterSource(transaction));
            return true;
        } catch (Exception e) {
            LogHelper.error(logger, "Error updating transaction: {}", e.getMessage());
        }
        return false;
    }

    @Override
    public Transaction fetchTransactionById(long transactionId) {
        LogHelper.info(logger, "Fetching transaction for ID: {}", transactionId);

        Transaction transaction = null;
        try {
            transaction = jdbcTemplate.queryForObject(buildFetchQuery(),
                    new BeanPropertySqlParameterSource(Transaction.builder().id((int) transactionId).build()),
                    new BeanPropertyRowMapper<>(Transaction.class));
            LogHelper.info(logger, "Transaction retrieved: {}", transaction);
        } catch (Exception e) {
            LogHelper.error(logger, "Failed to fetch transaction: {}", e.getMessage());
        }
        return transaction;
    }

    @Override
    public void updateTransactionReference(Transaction transaction) {
        try {
            jdbcTemplate.update(buildReferenceUpdateQuery(), new BeanPropertySqlParameterSource(transaction));
        } catch (Exception e) {
            LogHelper.error(logger, "Error updating transaction reference: {}", e.getMessage());
        }
    }

    @Override
    public Transaction fetchTransactionByReference(String reference) {
        LogHelper.info(logger, "Fetching transaction for reference: {}", reference);

        Transaction transaction = null;
        try {
            transaction = jdbcTemplate.queryForObject(buildReferenceFetchQuery(),
                    new BeanPropertySqlParameterSource(Transaction.builder().providerReference(reference).build()),
                    new BeanPropertyRowMapper<>(Transaction.class));
            LogHelper.info(logger, "Transaction retrieved: {}", transaction);
        } catch (Exception e) {
            LogHelper.error(logger, "Failed to fetch transaction by reference: {}", e.getMessage());
        }
        return transaction;
    }

    @Override
    public void updateTransactionStatus(Transaction transaction) {
        try {
            jdbcTemplate.update(buildStatusUpdateQuery(), new BeanPropertySqlParameterSource(transaction));
        } catch (Exception e) {
            LogHelper.error(logger, "Error updating transaction status: {}", e.getMessage());
        }
    }

    private String buildInsertQuery() {
        return "INSERT INTO Transaction (userId, paymentMethodId, providerId, paymentTypeId, amount, currency, txnStatusId, txnReference, txnDetailsId, debitorAccount, creditorAccount, merchantTransactionReference) " +
                "VALUES (:userId, :paymentMethodId, :providerId, :paymentTypeId, :amount, :currency, :txnStatusId, :txnReference, :txnDetailsId, :debitorAccount, :creditorAccount, :merchantTransactionReference)";
    }

    private String buildUpdateQuery() {
        return "UPDATE Transaction SET txnStatusId = :txnStatusId, txnDetailsId = :txnDetailsId WHERE id = :id";
    }

    private String buildFetchQuery() {
        return "SELECT * FROM Transaction WHERE id = :id";
    }

    private String buildReferenceUpdateQuery() {
        return "UPDATE Transaction SET providerReference = :providerReference WHERE id = :id";
    }

    private String buildReferenceFetchQuery() {
        return "SELECT * FROM Transaction WHERE providerReference = :providerReference";
    }

    private String buildStatusUpdateQuery() {
        return "UPDATE Transaction SET providerCode = :providerCode, providerMessage = :providerMessage WHERE id = :id";
    }
}