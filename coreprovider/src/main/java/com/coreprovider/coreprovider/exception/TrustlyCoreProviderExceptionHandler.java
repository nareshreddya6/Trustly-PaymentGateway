package com.cpt.payments.exception;

import com.cpt.payments.constants.ErrorCodeEnum;
import com.cpt.payments.pojo.response.error.*;
import com.cpt.payments.util.JsonUtils;
import com.cpt.payments.util.LogMessage;
import com.cpt.payments.util.SignatureCreator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for Trustly Core Provider service.
 * Handles validation and generic exceptions by converting them into standardized error responses.
 */
@ControllerAdvice
public class TrustlyCoreProviderExceptionHandler {
    
    private final Logger log = LogManager.getLogger(getClass());
    private final SignatureCreator signatureCreator;
    
    public TrustlyCoreProviderExceptionHandler(SignatureCreator signatureCreator) {
        this.signatureCreator = signatureCreator;
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<TrustlyErrorResponse> handleValidation(ValidationException ex) {
        log.warn("Validation failed: {}", ex.getErrorMessage());
        
        // Build error data with validation details
        var errorData = buildErrorData(ex.getErrorCode(), ex.getErrorMessage());
        
        // Generate signature for the error response
        String signature = generateSignature(ex.getMethod(), ex.getUuid(), errorData);
        
        // Construct the complete error response
        var response = assembleErrorResponse(
            ex.getMethod(),
            ex.getUuid(),
            signature,
            errorData,
            ex.getErrorCode(),
            ex.getErrorMessage()
        );
        
        log.debug("Returning validation error response: {}", response);
        return new ResponseEntity<>(response, ex.getHttpStatus());
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<TrustlyErrorResponse> handleUnexpectedErrors(Exception ex) {
        log.error("Unexpected error occurred", ex);
        
        var errorCode = ErrorCodeEnum.GENERIC_EXCEPTION;
        var errorData = buildErrorData(
            errorCode.getErrorCode(),
            errorCode.getErrorMessage()
        );
        
        // For generic exceptions, we use default values
        var response = assembleErrorResponse(
            "Deposit",  // Default method
            null,       // No UUID for generic errors
            "R9+hjuMqbsH0Ku ... S16VbzRsw==", // Default signature
            errorData,
            errorCode.getErrorCode(),
            errorCode.getErrorMessage()
        );
        
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    private ErrorData buildErrorData(String code, String message) {
        return ErrorData.builder()
            .code(code)
            .message(message)
            .build();
    }
    
    private String generateSignature(String method, String uuid, ErrorData errorData) {
        try {
            String serializedData = signatureCreator.serializeData(JsonUtils.toJsonNode(errorData));
            String plainText = method + uuid + serializedData;
            String signature = signatureCreator.generateSignature(plainText);
            
            log.debug("Generated signature for method: {}, uuid: {}", method, uuid);
            return signature;
        } catch (Exception e) {
            log.error("Failed to generate signature", e);
            return null;
        }
    }
    
    private TrustlyErrorResponse assembleErrorResponse(
            String method,
            String uuid,
            String signature,
            ErrorData errorData,
            String errorCode,
            String errorMessage) {
        
        var errorDetails = ErrorDetails.builder()
            .uuid(uuid)
            .signature(signature)
            .method(method)
            .data(errorData)
            .build();
            
        var errorWrapper = ErrorWrapper.builder()
            .name("JSONRPCError")
            .code(errorCode)
            .message(errorMessage)
            .error(errorDetails)
            .build();
            
        return TrustlyErrorResponse.builder()
            .version("1.1")
            .error(errorWrapper)
            .build();
    }
}