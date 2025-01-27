package com.cpt.payments.constants;

import com.cpt.payments.service.Validator;
import com.cpt.payments.service.impl.validators.AmountValidator;
import com.cpt.payments.service.impl.validators.CreditorAccountNumberValidator;
import com.cpt.payments.service.impl.validators.CurrencyValidator;
import com.cpt.payments.service.impl.validators.DebitorAccountNumberValidator;
import com.cpt.payments.service.impl.validators.DuplicateTransactionValidator;
import com.cpt.payments.service.impl.validators.EmailValidator;
import com.cpt.payments.service.impl.validators.FirstNameValidator;
import com.cpt.payments.service.impl.validators.LastNameValidator;
import com.cpt.payments.service.impl.validators.MerchantTransactionIDValidator;
import com.cpt.payments.service.impl.validators.PaymentMethodValidator;
import com.cpt.payments.service.impl.validators.PaymentRequestValidator;
import com.cpt.payments.service.impl.validators.PaymentTypeValidator;
import com.cpt.payments.service.impl.validators.PhoneNumberValidator;
import com.cpt.payments.service.impl.validators.ProviderIdValidator;
import com.cpt.payments.service.impl.validators.SignatureValidator;

import lombok.Getter;

public enum ValidatorType {

    SIGNATURE_VALIDATION("SIGNATURE_VALIDATION", SignatureValidator.class),
    REQUEST_VALIDATION("REQUEST_VALIDATION", PaymentRequestValidator.class),
    DUPLICATE_TXN_VALIDATION("DUPLICATE_TXN_VALIDATION", DuplicateTransactionValidator.class),
    MERCHANT_TXN_ID_VALIDATION("MERCHANT_TXN_ID_VALIDATION", MerchantTransactionIDValidator.class),
    FIRST_NAME_VALIDATION("FIRST_NAME_VALIDATION", FirstNameValidator.class),
    LAST_NAME_VALIDATION("LAST_NAME_VALIDATION", LastNameValidator.class),
    EMAIL_VALIDATION("EMAIL_VALIDATION", EmailValidator.class),
    PHONE_NUMBER_VALIDATION("PHONE_NUMBER_VALIDATION", PhoneNumberValidator.class),
    PAYMENT_METHOD_VALIDATION("PAYMENT_METHOD_VALIDATION", PaymentMethodValidator.class),
    PAYMENT_TYPE_VALIDATION("PAYMENT_TYPE_VALIDATION", PaymentTypeValidator.class),
    AMOUNT_VALIDATION("AMOUNT_VALIDATION", AmountValidator.class),
    CURRENCY_VALIDATION("CURRENCY_VALIDATION", CurrencyValidator.class),
    PROVIDER_VALIDATION("PROVIDER_VALIDATION", ProviderIdValidator.class),
    CREDITOR_ACCOUNT_VALIDATION("CREDITOR_ACCOUNT_VALIDATION", CreditorAccountNumberValidator.class),
    DEBITOR_ACCOUNT_VALIDATION("DEBITOR_ACCOUNT_VALIDATION", DebitorAccountNumberValidator.class);

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
