package com.example.ProviderTrustly.service.formatter.request;

import com.example.ProviderTrustly.constants.Constants;
import com.example.ProviderTrustly.constants.ErrorCodeEnum;
import com.example.ProviderTrustly.exception.PaymentProcessException;
import com.example.ProviderTrustly.http.HttpRequest;
import com.example.ProviderTrustly.pojo.request.*;
import com.example.ProviderTrustly.service.RequestFormatter;
import com.example.ProviderTrustly.util.JsonUtils;
import com.example.ProviderTrustly.util.LogMessage;
import com.example.ProviderTrustly.util.SignatureCreator;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Qualifier("InitiatePaymentRequestHandler")
public class InitiatePaymentRequestHandler implements RequestFormatter {
    
    private static final Logger logger = LogManager.getLogger(InitiatePaymentRequestHandler.class);
    private static final String DEFAULT_LOCALE = "en";
    private static final String DEFAULT_COUNTRY = "LT";
    
    @Value("${trustly.initiate.payment.url}")
    private String paymentEndpoint;
    
    @Autowired
    private Gson gson;
    
    @Autowired
    private SignatureCreator signatureCreator;

    @Override
    public HttpRequest prepareRequest(TrustlyProviderRequest paymentRequest) {
        String requestBody = createPaymentRequestBody(paymentRequest);
        logger.info("Payment request payload prepared: {}", requestBody);
        
        return HttpRequest.builder()
                .httpMethod(HttpMethod.POST)
                .request(requestBody)
                .url(paymentEndpoint)
                .build();
    }

    private String createPaymentRequestBody(TrustlyProviderRequest paymentRequest) {
        logger.info("Creating payment request body");
        
        String transactionRef = paymentRequest.getTransactionReference();
        Attributes paymentAttributes = buildPaymentAttributes(paymentRequest);
        Data paymentData = buildPaymentData(paymentRequest, paymentAttributes);
        String signature = generatePaymentSignature(paymentData, transactionRef);
        
        CoreTrustlyProvider requestBody = CoreTrustlyProvider.builder()
                .method(Constants.METHOD_DEPOSIT)
                .params(buildRequestParams(paymentData, signature, transactionRef))
                .version("1.1")
                .build();

        logger.debug("Payment request body created: {}", requestBody);
        return gson.toJson(requestBody);
    }

    private Attributes buildPaymentAttributes(TrustlyProviderRequest request) {
        String transactionRef = request.getTransactionReference();
        String baseUrl = "https://somedomain.com";
        
        return Attributes.builder()
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .email(request.getEmail())
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .country(DEFAULT_COUNTRY)
                .locale(DEFAULT_LOCALE)
                .successURL(String.format("%s/success/trustly/%s", baseUrl, transactionRef))
                .failURL(String.format("%s/failure/trustly/%s", baseUrl, transactionRef))
                .build();
    }

    private Data buildPaymentData(TrustlyProviderRequest request, Attributes attributes) {
        String transactionRef = request.getTransactionReference();
        
        return Data.builder()
                .attributes(attributes)
                .endUserID(transactionRef)
                .messageID(transactionRef)
                .notificationURL(String.format("https://somedomain.com/trustly/notify%s", transactionRef))
                .username("CTPuser")
                .password("CTPpassword")
                .build();
    }

    private String generatePaymentSignature(Data paymentData, String transactionRef) {
        try {
            String serializedData = signatureCreator.serializeData(JsonUtils.toJsonNode(paymentData));
            String signatureInput = Constants.METHOD_DEPOSIT + transactionRef + serializedData;
            
            logger.debug("Generating signature for: {}", signatureInput);
            return signatureCreator.generateSignature(signatureInput);
            
        } catch (Exception e) {
            logger.error("Failed to generate payment signature", e);
            throw new PaymentProcessException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCodeEnum.FAILED_TO_CREATE_SIGNATURE.getErrorCode(),
                    ErrorCodeEnum.FAILED_TO_CREATE_SIGNATURE.getErrorMessage()
            );
        }
    }

    private Params buildRequestParams(Data data, String signature, String transactionRef) {
        return Params.builder()
                .data(data)
                .signature(signature)
                .uuid(transactionRef)
                .build();
    }
}