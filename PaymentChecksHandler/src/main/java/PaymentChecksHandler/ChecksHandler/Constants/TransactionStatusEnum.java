package main.java.PaymentChecksHandler.ChecksHandler.Constants;

import lombok.Getter;

public enum TransactionStatusEnum {

    CREATED(1, "CREATED"),
    PENDING(2, "PENDING"),
    APPROVED(3, "APPROVED"),
    FAILED(4, "FAILED");

    @Getter
    private Integer id;

    @Getter
    private String name;

    private TransactionStatusEnum(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

}
