package com.example.ProviderTrustly.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown during payment processing to indicate various error
 * conditions
 * that may occur during the payment flow.
 */
public class PaymentProcessException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final transient HttpStatus status;
	private final String code;
	private final String message;
	private final boolean isThirdPartyError;

	/**
	 * Creates a new payment process exception with detailed error information
	 */
	public PaymentProcessException(HttpStatus status, String code,
			String message, boolean isThirdPartyError) {
		super(message);
		this.status = status;
		this.code = code;
		this.message = message;
		this.isThirdPartyError = isThirdPartyError;
	}

	/**
	 * Creates a new payment process exception for internal errors
	 */
	public PaymentProcessException(HttpStatus status, String code, String message) {
		this(status, code, message, false);
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

	public boolean isThirdPartyError() {
		return isThirdPartyError;
	}
}