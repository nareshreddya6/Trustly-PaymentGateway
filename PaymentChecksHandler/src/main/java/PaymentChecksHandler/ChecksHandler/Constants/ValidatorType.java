package com.cpt.payments.constants;

import com.cpt.payments.service.Checkers;
import com.cpt.payments.service.impl.Checkers.AmountVal;
import com.cpt.payments.service.impl.Checkers.CreditorAccountNumberVal;
import com.cpt.payments.service.impl.Checkers.CurrencyVal;
import com.cpt.payments.service.impl.Checkers.DebitorAccountNumberVal;
import com.cpt.payments.service.impl.Checkers.DuplicateTransactionVal;
import com.cpt.payments.service.impl.Checkers.EmailVal;
import com.cpt.payments.service.impl.Checkers.FirstNameVal;
import com.cpt.payments.service.impl.Checkers.LastNameVal;
import com.cpt.payments.service.impl.Checkers.MerchantTransactionIDVal;
import com.cpt.payments.service.impl.Checkers.PaymentMethodVal;
import com.cpt.payments.service.impl.Checkers.PaymentRequestVal;
import com.cpt.payments.service.impl.Checkers.PaymentTypeVal;
import com.cpt.payments.service.impl.Checkers.PhoneNumberVal;
import com.cpt.payments.service.impl.Checkers.ProviderIdVal;
import com.cpt.payments.service.impl.Checkers.SignatureVal;

import lombok.Getter;

public enum ValidatorType {

    SIGNATURE_VALIDATION("SIGNATURE_VALIDATION", SignatureVal.class),
    REQUEST_VALIDATION("REQUEST_VALIDATION", PaymentRequestVal.class),
    DUPLICATE_TXN_VALIDATION("DUPLICATE_TXN_VALIDATION", DuplicateTransactionVal.class),
    MERCHANT_TXN_ID_VALIDATION("MERCHANT_TXN_ID_VALIDATION", MerchantTransactionIDVal.class),
    FIRST_NAME_VALIDATION("FIRST_NAME_VALIDATION", FirstNameVal.class),
    LAST_NAME_VALIDATION("LAST_NAME_VALIDATION", LastNameVal.class),
    EMAIL_VALIDATION("EMAIL_VALIDATION", EmailVal.class),
    PHONE_NUMBER_VALIDATION("PHONE_NUMBER_VALIDATION", PhoneNumberVal.class),
    PAYMENT_METHOD_VALIDATION("PAYMENT_METHOD_VALIDATION", PaymentMethodVal.class),
    PAYMENT_TYPE_VALIDATION("PAYMENT_TYPE_VALIDATION", PaymentTypeVal.class),
    AMOUNT_VALIDATION("AMOUNT_VALIDATION", AmountVal.class),
    CURRENCY_VALIDATION("CURRENCY_VALIDATION", CurrencyVal.class),
    PROVIDER_VALIDATION("PROVIDER_VALIDATION", ProviderIdVal.class),
    CREDITOR_ACCOUNT_VALIDATION("CREDITOR_ACCOUNT_VALIDATION", CreditorAccountNumberVal.class),
    DEBITOR_ACCOUNT_VALIDATION("DEBITOR_ACCOUNT_VALIDATION", DebitorAccountNumberVal.class);

    @Getter
    private final String typeName;

    private final Class<? extends Validator> associatedValidator;

    ValidatorType(String typeName, Class<? extends Validator> associatedValidator) {
        this.typeName = typeName;
        this.associatedValidator = associatedValidator;
    }

    /**
     * Retrieve the appropriate ValidatorType based on the name provided.
     *
     * @param name The name of the validator.
     * @return The corresponding ValidatorType or null if not found.
     */
    public static ValidatorType fromName(String name) {
        for (ValidatorType type : ValidatorType.values()) {
            if (type.typeName.equals(name)) {
                return type;
            }
        }
        return null;
    }

    public Class<? extends Validator> getAssociatedValidator() {
        return associatedValidator;
    }
}
