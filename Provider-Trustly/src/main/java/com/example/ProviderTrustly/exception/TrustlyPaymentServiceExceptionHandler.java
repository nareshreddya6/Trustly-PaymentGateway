package com.example.ProviderTrustly.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.ProviderTrustly.constants.ErrorCodeEnum;
import com.example.ProviderTrustly.pojo.ErrorResponse;
import com.example.ProviderTrustly.util.LogMessage;

/**
 * Global exception handler for Trustly payment service.
 * Handles both specific payment processing errors and generic exceptions.
 */
@ControllerAdvice
public class TrustlyPaymentServiceExceptionHandler {
    
    private static final Logger log = LogManager.getLogger(TrustlyPaymentServiceExceptionHandler.class);

    /**
     * Handles specific payment processing exceptions
     */
    @ExceptionHandler(PaymentProcessException.class)
    public ResponseEntity<ErrorResponse> handlePaymentError(PaymentProcessException e) {
        log.error("Payment processing failed: {}", e.getMessage());
        
        var response = buildErrorResponse(
            e.getErrorCode(),
            e.getErrorMessage(),
            e.isTpProviderError()
        );
        
        log.debug("Returning error response: {}", response);
        return ResponseEntity
            .status(e.getHttpStatus())
            .body(response);
    }

    /**
     * Fallback handler for all unhandled exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedError(Exception e) {
        log.error("Unexpected error occurred: ", e);
        
        var genericError = ErrorCodeEnum.GENERIC_EXCEPTION;
        var response = buildErrorResponse(
            genericError.getErrorCode(),
            genericError.getErrorMessage(),
            false
        );
        
        log.debug("Returning generic error response: {}", response);
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(response);
    }
    
    private ErrorResponse buildErrorResponse(String code, String message, boolean isProviderError) {
        return ErrorResponse.builder()
            .errorCode(code)
            .errorMessage(message)
            .tpProviderError(isProviderError)
            .build();
    }
}