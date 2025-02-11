package com.example.BillingService.Constants;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;

@RequiredArgsConstructor
public enum TransactionStatusEnum {
    CREATED(1, "CREATED"),
    PENDING(2, "PENDING"),
    APPROVED(3, "APPROVED"),
    FAILED(4, "FAILED");

    @Getter private final Integer id;
    @Getter private final String name;

    public static TransactionStatusEnum getTransactionStatusEnum(int transactionStatusId) {
        return Arrays.stream(values())
                .filter(e -> e.id == transactionStatusId)
                .findFirst()
                .orElse(null);
    }
}
