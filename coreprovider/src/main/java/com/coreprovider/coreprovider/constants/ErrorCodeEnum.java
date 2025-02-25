package com.coreprovider.coreprovider.constants;

import lombok.Getter;

/**
 * Represents system-wide error codes and their corresponding messages
 * for standardized error handling across the application.
 */
@Getter
public enum ErrorCodeEnum {
    // System level errors (3xxxx)
    SYSTEM_GENERAL_ERROR("30001", "An unexpected error occurred. Please try again later"),
    TRUSTLY_CONNECTION_ERROR("30002", "Unable to establish connection with payment provider"),
    TRUSTLY_PAYMENT_INITIALIZATION_ERROR("30003", "Payment initialization failed at provider end"),
    
    // Security related errors (6xx)
    RSA_SIGNATURE_VERIFICATION_ERROR("636", "Digital signature verification failed");
    
    private final String code;
    private final String description;
    
    /**
     * Constructs an error code enum with the specified code and description.
     *
     * @param code unique identifier for the error
     * @param description human-readable error message
     */
    ErrorCodeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
}