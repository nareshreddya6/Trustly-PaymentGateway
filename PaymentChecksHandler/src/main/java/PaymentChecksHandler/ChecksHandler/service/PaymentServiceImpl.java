package com.cpt.payments.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cpt.payments.constants.ErrorCodeEnum;
import com.cpt.payments.constants.PaymentMethodEnum;
import com.cpt.payments.constants.PaymentTypeEnum;
import com.cpt.payments.constants.ProviderEnum;
import com.cpt.payments.constants.TransactionStatusEnum;
import com.cpt.payments.constants.ValidatorType;
import com.cpt.payments.exceptions.ValidationException;
import com.cpt.payments.http.HttpRequest;
import com.cpt.payments.http.HttpRestTemplateEngine;
import com.cpt.payments.pojo.PaymentRequest;
import com.cpt.payments.pojo.PaymentResponse;
import com.cpt.payments.pojo.ProcessingServiceRequest;
import com.cpt.payments.pojo.TransactionReqRes;
import com.cpt.payments.pojo.processing.response.ErrorResponse;
import com.cpt.payments.service.PaymentService;
import com.cpt.payments.service.Supplier;
import com.cpt.payments.service.UserService;
import com.cpt.payments.service.Validator;
import com.cpt.payments.util.LogMessage;
import com.google.gson.Gson;

import main.java.PaymentChecksHandler.ChecksHandler.Constants.ValidatorType;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);

    @Autowired
    private ApplicationContext appContext;

    @Value("${payment.validators}")
    private String validatorConfig;

    @Value("${payment.processing.service.initiate.payment}")
    private String paymentInitiationEndpoint;

    @Value("${payment.processing.service.process.payment}")
    private String paymentProcessingEndpoint;

    @Autowired
    private UserService userManagementService;

    @Autowired
    private HttpRestTemplateEngine restTemplateEngine;

    @Override
    public PaymentResponse CheckAndInitiatepay(PaymentRequest request) {
        LogMessage.log(LOGGER, "Configured validators: " + validatorConfig);

        List<String> validatorNames = getValidatorNames(validatorConfig);
        validatePaymentRequest(request, validatorNames);

        Long generatedUserId = userManagementService.createUser(request);

        TransactionReqRes transactionData = initiateTransaction(generatedUserId, request);

        PaymentResponse finalResponse = executeTransaction(transactionData, request);
        return finalResponse;
    }

    private List<String> getValidatorNames(String validators) {
        return new ArrayList<>(Arrays.asList(validators.split(",")));
    }

    private void validatePaymentRequest(PaymentRequest request, List<String> validatorNames) {
        for (String validator : validatorNames) {
            ValidatorType validatortype = ValidatorType.fromName(validator);
            Validator validatorInstance = getValidatorInstance(validatortype);
            validatorInstance.performValidation(request);
        }
    }

    private Validator getValidatorInstance(ValidatorType validatorEnum) {
        return appContext.getBean(validatorEnum.getAssociatedValidator());
    }

    private PaymentResponse executeTransaction(TransactionReqRes transaction, PaymentRequest request) {
        ProcessingServiceRequest processingRequest = ProcessingServiceRequest.builder()
            .transactionId(transaction.getId())
            .firstName(request.getUser().getFirstName())
            .lastName(request.getUser().getLastName())
            .email(request.getUser().getEmail())
            .build();

        Gson gson = new Gson();
        HttpRequest httpRequest = HttpRequest.builder()
            .url(paymentProcessingEndpoint)
            .httpMethod(HttpMethod.POST)
            .request(gson.toJson(processingRequest))
            .build();

        ResponseEntity<String> response = restTemplateEngine.execute(httpRequest);
        if (response == null || response.getBody() == null) {
            LogMessage.log(LOGGER, "Transaction processing failed for transaction: " + transaction);
            throw new ValidationException(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCodeEnum.GENERIC_EXCEPTION.getErrorCode(),
                    ErrorCodeEnum.GENERIC_EXCEPTION.getErrorMessage());
        }

        if (response.getStatusCodeValue() != HttpStatus.OK.value()) {
            LogMessage.debug(LOGGER, "Transaction processing error. Response code: " + response.getStatusCodeValue());
            ErrorResponse errorDetails = gson.fromJson(response.getBody(), ErrorResponse.class);
            LogMessage.log(LOGGER, "Error response: " + errorDetails);
            throw new ValidationException(HttpStatus.BAD_GATEWAY,
                    errorDetails.getErrorCode(),
                    errorDetails.getErrorMessage());
        }

        PaymentResponse processedResponse = gson.fromJson(response.getBody(), PaymentResponse.class);
        LogMessage.log(LOGGER, "Transaction response: " + processedResponse);
        return processedResponse;
    }

    private TransactionReqRes initiateTransaction(Long userId, PaymentRequest request) {
        TransactionReqRes transactionRequest = TransactionReqRes.builder()
            .amount(Double.parseDouble(request.getPayment().getAmount()))
            .creditorAccount(request.getPayment().getCreditorAccount())
            .debitorAccount(request.getPayment().getDebitorAccount())
            .currency(request.getPayment().getCurrency())
            .paymentMethodId(PaymentMethodEnum.getPaymentMethod(request.getPayment().getPaymentMethod()).getPaymentMethodId())
            .paymentTypeId(PaymentTypeEnum.getPaymentType(request.getPayment().getPaymentType()).getPaymentTypeId())
            .providerId(ProviderEnum.getProviderEnum(request.getPayment().getProviderId()).getProviderId())
            .txnReference(UUID.randomUUID().toString())
            .txnStatusId(TransactionStatusEnum.CREATED.getId())
            .userId(userId)
            .merchantTransactionReference(request.getPayment().getMerchantTransactionReference())
            .build();

        LogMessage.log(LOGGER, "Initiating transaction: " + transactionRequest);

        Gson gson = new Gson();
        HttpRequest httpRequest = HttpRequest.builder()
            .url(paymentInitiationEndpoint)
            .httpMethod(HttpMethod.POST)
            .request(gson.toJson(transactionRequest))
            .build();

        ResponseEntity<String> response = restTemplateEngine.execute(httpRequest);
        if (response == null || response.getBody() == null) {
            LogMessage.log(LOGGER, "Transaction initiation failed: " + transactionRequest);
            throw new ValidationException(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCodeEnum.FAILED_TO_CREATE_TRANSACTION.getErrorCode(),
                    ErrorCodeEnum.FAILED_TO_CREATE_TRANSACTION.getErrorMessage());
        }

        if (response.getStatusCodeValue() != HttpStatus.OK.value()) {
            LogMessage.debug(LOGGER, "Transaction initiation error. Response code: " + response.getStatusCodeValue());
            ErrorResponse errorDetails = gson.fromJson(response.getBody(), ErrorResponse.class);
            LogMessage.log(LOGGER, "Error response: " + errorDetails);
            throw new ValidationException(HttpStatus.BAD_GATEWAY,
                    errorDetails.getErrorCode(),
                    errorDetails.getErrorMessage());
        }

        TransactionReqRes createdTransaction = gson.fromJson(response.getBody(), TransactionReqRes.class);
        LogMessage.log(LOGGER, "Transaction initiated with ID: " + createdTransaction.getId());
        return createdTransaction;
    }
}
