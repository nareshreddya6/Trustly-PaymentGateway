package com.cpt.payments.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import main.java.PaymentChecksHandler.ChecksHandler.exceptions.ValidationException;
import main.java.PaymentChecksHandler.ChecksHandler.http.HttpRestTemplateEngine;
import main.java.PaymentChecksHandler.ChecksHandler.models.PaymentRequest;
import main.java.PaymentChecksHandler.ChecksHandler.models.PaymentResponse;
import main.java.PaymentChecksHandler.ChecksHandler.models.TransactionReqRes;
import main.java.PaymentChecksHandler.ChecksHandler.service.impl.PaymentServiceImpl;
import PaymentChecksHandler.ChecksHandler.TestDataProviderUtil;
import com.google.gson.Gson;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceImplTest {

    @Mock
    private UserService userService;
    @Mock
    private HttpRestTemplateEngine httpRestTemplateEngine;
    @Mock
    private ApplicationContext context;
    @Mock
    private Validator validator;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private final Gson gson = new Gson();
    private static final String VALIDATORS = "SIGNATURE_CHECK_FILTER,PAYMENT_REQUEST_FILTER,DUPLICATE_TXN_FILTER," +
            "MERCHANT_TXN_ID_FILTER,FIRST_NAME_FILTER,LAST_NAME_FILTER,CUSTOMER_EMAIL_FILTER,PHONE_NUMBER_FILTER," +
            "PAYMENT_METHOD_FILTER,PAYMENT_TYPE_FILTER,AMOUNT_FILTER,CURRENCY_FILTER,PROVIDER_ID_FILTER," +
            "CREDITOR_ACCOUNT_NUMBER,DEBITOR_ACCOUNT_NUMBER";

    @Test
    @DisplayName("Validate and Initiate Payment Successfully")
    public void validateAndInitiatePaymentSuccessfully() {
        PaymentRequest paymentRequest = createPaymentRequest();
        TransactionReqRes transaction = new TransactionReqRes();
        transaction.setId(456);

        PaymentResponse expectedResponse = new PaymentResponse();
        expectedResponse.setPaymentReference("testPaymentReference");

        ReflectionTestUtils.setField(paymentService, "validators", VALIDATORS);
        ReflectionTestUtils.setField(paymentService, "context", context);

        when(userService.createUser(any())).thenReturn(123L);
        when(httpRestTemplateEngine.execute(any())).thenReturn(
                ResponseEntity.ok(gson.toJson(transaction)),
                ResponseEntity.ok(gson.toJson(expectedResponse)));
        when(context.getBean(any(Class.class))).thenReturn(validator);
        doNothing().when(validator).doValidate(any());

        PaymentResponse actualResponse = paymentService.validateAndInitiatePayment(paymentRequest);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getPaymentReference(), actualResponse.getPaymentReference());
    }

    @Test
    @DisplayName("Initiate Transaction fails")
    public void validateAndInitiatePaymentInitFail() {
        PaymentRequest paymentRequest = createPaymentRequest();

        ReflectionTestUtils.setField(paymentService, "validators", VALIDATORS);
        ReflectionTestUtils.setField(paymentService, "context", context);

        when(userService.createUser(any())).thenReturn(123L);
        when(context.getBean(any(Class.class))).thenReturn(validator);
        when(httpRestTemplateEngine.execute(any())).thenReturn(ResponseEntity.internalServerError().build());
        doNothing().when(validator).doValidate(any());

        assertThrows(ValidationException.class, () -> paymentService.validateAndInitiatePayment(paymentRequest));
    }

    private PaymentRequest createPaymentRequest() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setUser(TestDataProviderUtil.getTestUserBean());
        paymentRequest.setPayment(TestDataProviderUtil.getTestPayment());
        return paymentRequest;
    }
}
