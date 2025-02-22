package com.example.BillingService.Constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ErrorEnums {
    GENERIC_EXCEPTION("20001", "Something went wrong, please try later"),
    PAYMENT_NOT_FOUND("20002", "Bad request, payment request not found"),
    PROVIDER_NOT_FOUND("20003", "Bad request, provider not found"),
    FAILED_TO_CREATE_TRANSACTION("20004", "Payment not created at provider"),
    TRANSACTION_STATUS_HANDLER_NOT_FOUND("20005", "Transaction status handler not found"),
    TRANSACTION_STATUS_UPDATE_FAILED("20006", "Transaction status update failed"),
    SIGNATURE_NOT_FOUND("20007", "Bad request, signature not found"),
    SIGNATURE_VERIFICATION_FAILED("20008", "Bad request, signature verification failed"),
    INVALID_SIGNATURE("20009", "Bad request, invalid signature"),
    TP_TRUSTLY_ERROR("20010", "Unable to process payment at Trustly");

    @Getter private final String errorCode;
    @Getter private final String errorMessage;
}
