package main.java.PaymentChecksHandler.ChecksHandler.Constants;

import lombok.Getter;

public enum PaymentMethodEnum {

    APM(1, "APM");

    @Getter
    private final Integer paymentMethodId;

    @Getter
    private final String paymentMethodName;

    PaymentMethodEnum(Integer paymentMethodId, String paymentMethodName) {
        this.paymentMethodId = paymentMethodId;
        this.paymentMethodName = paymentMethodName;
    }

    public static PaymentMethodEnum getPaymentMethod(String paymentMethod) {
        return findPaymentMethod(paymentMethod);
    }

    private static PaymentMethodEnum findPaymentMethod(String paymentMethod) {
        for (PaymentMethodEnum method : PaymentMethodEnum.values()) {
            if (method.paymentMethodName.equalsIgnoreCase(paymentMethod)) {
                return method;
            }
        }
        return null;
    }
}
