package com.cpt.payments.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import main.java.PaymentChecksHandler.ChecksHandler.constants.ErrorCodeEnum;
import main.java.PaymentChecksHandler.ChecksHandler.models.PaymentError;
import main.java.PaymentChecksHandler.ChecksHandler.models.PaymentResponse;
import main.java.PaymentChecksHandler.ChecksHandler.util.LogMessageUtil;

@ControllerAdvice
public class PaymentExceptionHandler {

    private static final Logger logger = LogManager.getLogger(PaymentExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<PaymentError> handlePaymentValidationError(ValidationException validationEx) {
        LogMessageUtil.logMessage(logger, "Validation issue: " + validationEx.getErrorMessage());

        PaymentError errorResponse = new PaymentError(validationEx.getErrorCode(), validationEx.getErrorMessage());

        LogMessageUtil.logMessage(logger, "Generated error response: " + errorResponse);

        return new ResponseEntity<>(errorResponse, validationEx.getHttpStatus());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<PaymentError> handleUnexpectedError(Exception unexpectedEx) {
        LogMessageUtil.logMessage(logger, "Unexpected error occurred: " + unexpectedEx.getMessage());
        LogMessageUtil.logExceptionDetails(logger, unexpectedEx);

        PaymentError genericErrorResponse = new PaymentError(
                ErrorCodeEnum.GENERIC_EXCEPTION.getErrorCode(),
                ErrorCodeEnum.GENERIC_EXCEPTION.getErrorMessage());

        LogMessageUtil.logMessage(logger, "Generated generic error response: " + genericErrorResponse);

        return new ResponseEntity<>(genericErrorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
