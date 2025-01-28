package main.java.PaymentChecksHandler.ChecksHandler.Exceptions;

import org.springframework.http.HttpStatus;

public class ValidationException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String errorCode;
    private final String errorMessage;

    // Constructor using constructor chaining
    public ValidationException(HttpStatus status, String code, String message) {
        super(message); // Set the message for the exception
        this.httpStatus = status;
        this.errorCode = code;
        this.errorMessage = message;
    }

    // Getters (No need for setters since fields are final)
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // Optionally override toString for better debugging info
    @Override
    public String toString() {
        return "ValidationException{" +
                "httpStatus=" + httpStatus +
                ", errorCode='" + errorCode + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
