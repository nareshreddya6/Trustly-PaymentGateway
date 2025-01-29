
package main.java.PaymentChecksHandler.Repository.DAL;

import main.java.PaymentChecksHandler.ChecksHandler.constants.MerchantPaymentSaveResult;

import main.java.PaymentChecksHandler.Repository.DTL.MerchantTransactionRequest;

public interface MerchantTransactionRequestDao {

    MerchantTransactionRequest getMerchantTransactionRequest(String merchantTransactionId);

    MerchantPaymentSaveResult insertMerchantTransactionRequest(MerchantTransactionRequest merchanttransactionrequest);

}
