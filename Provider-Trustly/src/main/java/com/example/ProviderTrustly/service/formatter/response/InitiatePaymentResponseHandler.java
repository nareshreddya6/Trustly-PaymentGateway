package com.example.ProviderTrustly.service.formatter.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.ProviderTrustly.constants.Constants;
import com.example.ProviderTrustly.constants.ErrorCodeEnum;
import com.example.ProviderTrustly.exception.PaymentProcessException;
import com.example.ProviderTrustly.pojo.response.TrustlyCoreResponse;
import com.example.ProviderTrustly.pojo.response.TrustlyProviderResponse;
import com.example.ProviderTrustly.pojo.response.error.ErrorDetails;
import com.example.ProviderTrustly.service.ResponseFormatter;
import com.example.ProviderTrustly.service.helper.PaymentServiceHelper;
import com.example.ProviderTrustly.util.LogMessage;
import com.google.gson.Gson;

@Service
@Qualifier("InitiatePaymentResponseHandler")
public class InitiatePaymentResponseHandler implements ResponseFormatter {
    
    private static final Logger logger = LogManager.getLogger(InitiatePaymentResponseHandler.class);
    private static final int HTTP_OK = 200;

    private final Gson gson;
    private final PaymentServiceHelper paymentHelper;

    @Autowired
    public InitiatePaymentResponseHandler(Gson gson, PaymentServiceHelper paymentHelper) {
        this.gson = gson;
        this.paymentHelper = paymentHelper;
    }

    @Override
    public TrustlyProviderResponse processResponse(ResponseEntity<String> response) {
        logger.info("Processing payment initiation response with status: {}", response.getStatusCodeValue());
        
        validateResponse(response);
        
        if (response.getStatusCodeValue() != HTTP_OK) {
            return handleErrorResponse(response);
        }
        
        return processSuccessfulResponse(response);
    }

    private void validateResponse(ResponseEntity<String> response) {
        if (response.getBody() == null) {
            logger.error("Empty response body received from Trustly");
            throw createPaymentException(
                HttpStatus.BAD_REQUEST,
                ErrorCodeEnum.FAILED_TO_INITIATE_PAYMENT_AT_TRUSTLY
            );
        }
    }

    private TrustlyProviderResponse processSuccessfulResponse(ResponseEntity<String> response) {
        TrustlyCoreResponse trustlyResponse = parseResponse(response);
        logger.debug("Parsed Trustly response: {}", trustlyResponse);

        verifyResponseSignature(
            trustlyResponse.getResult().getSignature(),
            trustlyResponse.getResult().getData(),
            trustlyResponse.getResult().getUuid()
        );

        return buildProviderResponse(trustlyResponse);
    }

    private TrustlyProviderResponse buildProviderResponse(TrustlyCoreResponse trustlyResponse) {
        return TrustlyProviderResponse.builder()
            .paymentId(trustlyResponse.getResult().getData().getOrderid())
            .redirectUrl(trustlyResponse.getResult().getData().getUrl())
            .build();
    }

    private TrustlyProviderResponse handleErrorResponse(ResponseEntity<String> response) {
        TrustlyCoreResponse errorResponse = parseResponse(response);
        
        if (errorResponse.getError() != null) {
            handleTrustlyError(errorResponse);
        }
        
        logger.error("Invalid response received from Trustly: {}", response);
        throw createPaymentException(
            HttpStatus.BAD_REQUEST,
            ErrorCodeEnum.FAILED_TO_INITIATE_PAYMENT_AT_TRUSTLY
        );
    }

    private void handleTrustlyError(TrustlyCoreResponse errorResponse) {
        ErrorDetails errorDetails = errorResponse.getError().getError();
        
        verifyResponseSignature(
            errorDetails.getSignature(),
            errorDetails.getData(),
            errorDetails.getUuid()
        );

        logger.error("Trustly error received: code={}, message={}", 
            errorDetails.getData().getCode(), 
            errorDetails.getData().getMessage()
        );
        
        throw new PaymentProcessException(
            HttpStatus.BAD_REQUEST,
            errorDetails.getData().getCode(),
            errorDetails.getData().getMessage(),
            true
        );
    }

    private void verifyResponseSignature(String signature, Object data, String uuid) {
        boolean isValid = paymentHelper.isResponseSignatureValid(
            signature, data, uuid, Constants.METHOD_DEPOSIT
        );

        if (!isValid) {
            logger.error("Invalid signature detected in Trustly response");
            throw createPaymentException(
                HttpStatus.UNAUTHORIZED,
                ErrorCodeEnum.FAILED_TO_PROCESS_TRUSTLY_SIGNATURE
            );
        }
        
        logger.debug("Response signature verification successful");
    }

    private TrustlyCoreResponse parseResponse(ResponseEntity<String> response) {
        return gson.fromJson(response.getBody(), TrustlyCoreResponse.class);
    }

    private PaymentProcessException createPaymentException(HttpStatus status, ErrorCodeEnum error) {
        return new PaymentProcessException(
            status,
            error.getErrorCode(),
            error.getErrorMessage()
        );
    }
}