package com.example.BillingService.Constants;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.Arrays;

@RequiredArgsConstructor
public enum ProviderEnum {
    TRUSTLY(1, "TRUSTLY");

    @Getter private final Integer providerId;
    @Getter private final String providerName;

    public static ProviderEnum getProviderEnum(String providerName) {
        return Arrays.stream(values())
                .filter(e -> e.providerName.equals(providerName))
                .findFirst()
                .orElse(null);
    }

    public static ProviderEnum getProviderEnumById(int id) {
        return Arrays.stream(values())
                .filter(e -> e.providerId == id)
                .findFirst()
                .orElse(null);
    }
}
