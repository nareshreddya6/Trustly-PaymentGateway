package main.java.PaymentChecksHandler.ChecksHandler.Constants;

import lombok.Getter;
import java.util.Map;
import java.util.HashMap;

public enum ProviderEnum {

    TRUSTLY(1, "TRUSTLY");

    @Getter
    private final Integer providerId;
    @Getter
    private final String providerName;

    private static final Map<String, ProviderEnum> PROVIDER_MAP = new HashMap<>();

    // Static block to initialize the PROVIDER_MAP
    static {
        for (ProviderEnum provider : ProviderEnum.values()) {
            PROVIDER_MAP.put(provider.providerName.toLowerCase(), provider);
        }
    }

    private ProviderEnum(Integer providerId, String providerName) {
        this.providerId = providerId;
        this.providerName = providerName;
    }

    // The method remains the same but utilizes the map for improved performance
    public static ProviderEnum getProviderEnum(String providerName) {
        return PROVIDER_MAP.get(providerName.toLowerCase());
    }
}
