package com.coreprovider.coreprovider.exception;

import org.springframework.http.HttpStatus;
import java.util.Objects;

/**
 * Custom exception for handling validation errors in the payment processing
 * flow.
 * This exception carries additional context about the validation failure
 * including
 * HTTP status, error details, and request tracking information.
 */
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final HttpStatus status;
    private final String code;
    private final String message;
    private final String requestId;
    private final String methodName;

    /**
     * Creates a new validation exception with detailed error context.
     *
     * @param status     HTTP status code for the error response
     * @param code       Error code identifying the type of validation failure
     * @param message    Detailed description of what went wrong
     * @param requestId  Unique identifier for tracking the request
     * @param methodName Name of the method where validation failed
     */
    public ValidationException(HttpStatus status, String code, String message,
            String requestId, String methodName) {
        super(message);
        this.status = Objects.requireNonNull(status, "HTTP status must not be null");
        this.code = code;
        this.message = message;
        this.requestId = requestId;
        this.methodName = methodName;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getMethodName() {
        return methodName;
    }

    @Override
    public String toString() {
        return String.format("ValidationException[status=%s, code='%s', message='%s', requestId='%s', method='%s']",
                status, code, message, requestId, methodName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof ValidationException))
            return false;
        ValidationException that = (ValidationException) o;
        return status == that.status &&
                Objects.equals(code, that.code) &&
                Objects.equals(message, that.message) &&
                Objects.equals(requestId, that.requestId) &&
                Objects.equals(methodName, that.methodName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, code, message, requestId, methodName);
    }
}