
package main.java.PaymentChecksHandler.Repository.DAL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import main.java.PaymentChecksHandler.ChecksHandler.constants.PaymentSaveOutcome;
import main.java.PaymentChecksHandler.ChecksHandler.Repository.DAL.MerchantTransactionRequestDao;
import main.java.PaymentChecksHandler.ChecksHandler.Repository.DTL.MerchantTransactionRequest;
import com.cpt.payments.util.LogMessage;

@Repository
public class MerchantTransactionRequestDaoImpl implements MerchantTransactionRequestDao {

    private static final Logger LOGGER = LogManager.getLogger(MerchantTransactionRequestDaoImpl.class);

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public MerchantTransactionRequest getMerchantPaymentRequest(String merchantTransactionId) {
        LogMessage.log(LOGGER, "Fetching MerchantPaymentRequest details for: " + merchantTransactionId);

        try {
            return namedJdbcTemplate.queryForObject(
                    getMerchantPaymentRequestQuery(),
                    new BeanPropertySqlParameterSource(
                            MerchantTransactionRequest.builder().merchantTransactionReference(merchantTransactionId)
                                    .build()),
                    new BeanPropertyRowMapper<>(MerchantTransactionRequest.class));
        } catch (Exception e) {
            LogMessage.log(LOGGER, "Unable to fetch MerchantPaymentRequest details", e);
            return null;
        }
    }

    private String getMerchantPaymentRequestQuery() {
        return "SELECT * FROM merchant_payment_request WHERE merchantTransactionReference = :merchantTransactionReference";
    }

    @Override
    public MerchantPaymentSaveResult insertMerchantPaymentRequest(
            MerchantTransactionRequest merchanttransactionrequest) {
        try {
            namedJdbcTemplate.update(
                    insertMerchantPaymentRequestQuery(),
                    new BeanPropertySqlParameterSource(merchanttransactionrequest));
            return PaymentSaveOutcome.IS_SAVED;
        } catch (DuplicateKeyException e) {
            LogMessage.log(LOGGER, "Duplicate entry detected while saving MerchantPaymentRequest", e);
            return PaymentSaveOutcome.IS_DUPLICATE;
        } catch (Exception e) {
            LogMessage.log(LOGGER, "Exception occurred while saving MerchantPaymentRequest", e);
            return PaymentSaveOutcome.IS_ERROR;
        }
    }

    private String insertMerchantPaymentRequestQuery() {
        return "INSERT INTO merchant_payment_request (merchantTransactionReference, transactionRequest) VALUES (:merchantTransactionReference, :transactionRequest)";
    }
}
