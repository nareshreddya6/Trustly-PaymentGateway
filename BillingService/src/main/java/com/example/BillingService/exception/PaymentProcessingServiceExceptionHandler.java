package com.example.BillingService.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.example.BillingService.constants.ErrorCodeEnum;
import com.example.BillingService.models.ErrorResponse;
import com.example.BillingService.models.PaymentResponse;
import com.example.BillingService.util.LogMessage;

/**
 * Global exception handler for the Payment Processing Service.
 * Handles both specific payment processing exceptions and generic errors.
 */
@ControllerAdvice
public class PaymentProcessingServiceExceptionHandler {

	private static final Logger logger = LogManager.getLogger(PaymentProcessingServiceExceptionHandler.class);

	/**
	 * Handles specific payment processing exceptions.
	 * 
	 * @param exception The payment processing exception
	 * @return ResponseEntity containing error details
	 */
	@ExceptionHandler(PaymentProcessingException.class)
	public ResponseEntity<ErrorResponse> handlePaymentException(PaymentProcessingException exception) {
		logger.error("Payment processing failed: {}", exception.getErrorMessage());

		ErrorResponse response = buildErrorResponse(
				exception.getErrorCode(),
				exception.getErrorMessage());

		logger.debug("Returning error response: {}", response);
		return new ResponseEntity<>(response, exception.getHttpStatus());
	}

	/**
	 * Fallback handler for all unhandled exceptions.
	 * 
	 * @param exception The caught exception
	 * @return ResponseEntity with generic error details
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleUnexpectedError(Exception exception) {
		logger.error("Unexpected error occurred: ", exception);

		ErrorResponse response = buildErrorResponse(
				ErrorCodeEnum.GENERIC_EXCEPTION.getErrorCode(),
				ErrorCodeEnum.GENERIC_EXCEPTION.getErrorMessage());

		logger.debug("Returning generic error response: {}", response);
		return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ErrorResponse buildErrorResponse(String code, String message) {
		return ErrorResponse.builder()
				.errorCode(code)
				.errorMessage(message)
				.build();
	}
}