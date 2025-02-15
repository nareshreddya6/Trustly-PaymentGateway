package com.example.BillingService.dao.Impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.BillingService.dao.TransactionDetailsDao;
import com.cpt.payments.util.LogHelper;

import com.example.BillingService.dao.TransactionDetailsDao;
import com.example.BillingService.dto.TransactionDetails;
import com.example.BillingService.util.LogHelper;

@Repository
public class TransactionDetailsDaoImpl implements TransactionDetailsDao {
	private static final Logger logger = LogManager.getLogger(TransactionDetailsDaoImpl.class);

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public TransactionDetails fetchTransactionByCode(String code) {
		LogHelper.info(logger, "Fetching transaction details for code: {}", code);

		TransactionDetails details = null;
		try {
			details = jdbcTemplate.queryForObject(buildFetchQuery(),
					new BeanPropertySqlParameterSource(TransactionDetails.builder().code(code).build()),
					new BeanPropertyRowMapper<>(TransactionDetails.class));
			LogHelper.info(logger, "Transaction details retrieved: {}", details);
		} catch (Exception e) {
			LogHelper.error(logger, "Failed to fetch transaction details: {}", e.getMessage());
		}
		return details;
	}

	private String buildFetchQuery() {
		String query = "SELECT * FROM Transaction_Details WHERE code = :code";
		LogHelper.debug(logger, "Fetch query: {}", query);
		return query;
	}
}