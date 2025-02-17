package com.example.BillingService.exception;

import org.springframework.http.HttpStatus;

/**
 * Custom exception for handling payment processing errors.
 * Encapsulates HTTP status, error code, and detailed error message.
 */
public class PaymentProcessingException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final HttpStatus status;
	private final String code;
	private final String message;

	/**
	 * Creates a new payment processing exception with status, code and message.
	 *
	 * @param status  HTTP status indicating the type of error
	 * @param code    Error code for identifying the specific error
	 * @param message Detailed description of the error
	 */
	public PaymentProcessingException(HttpStatus status, String code, String message) {
		super(message);
		this.status = status;
		this.code = code;
		this.message = message;
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
}