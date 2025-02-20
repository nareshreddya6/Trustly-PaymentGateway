package com.example.ProviderTrustly.service.helper;

import com.example.ProviderTrustly.dao.ProviderRequestResponseDao;
import com.example.ProviderTrustly.dto.ProviderRequestResponseDto;
import com.example.ProviderTrustly.http.HttpRequest;
import com.example.ProviderTrustly.util.JsonUtils;
import com.example.ProviderTrustly.util.LogMessage;
import com.example.ProviderTrustly.util.SHA256RSASignatureVerifier;
import com.example.ProviderTrustly.util.SignatureCreator;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Helper class for payment service operations including request/response handling
 * and signature validation.
 */
@Component
public class PaymentServiceHelper {
    
    private final Logger log = LogManager.getLogger(getClass());
    
    private final ProviderRequestResponseDao responseDao;
    private final Gson gson;
    private final SignatureCreator signatureCreator;
    private final SHA256RSASignatureVerifier signatureVerifier;

    @Autowired
    public PaymentServiceHelper(ProviderRequestResponseDao responseDao, 
                              Gson gson,
                              SignatureCreator signatureCreator,
                              SHA256RSASignatureVerifier signatureVerifier) {
        this.responseDao = responseDao;
        this.gson = gson;
        this.signatureCreator = signatureCreator;
        this.signatureVerifier = signatureVerifier;
    }

    /**
     * Stores the provider request and response details in the database
     */
    public void saveProviderRequestResponse(String txnRef, HttpRequest request, ResponseEntity<String> response) {
        var requestResponse = ProviderRequestResponseDto.builder()
            .transactionReference(txnRef)
            .request(gson.toJson(request))
            .response(response.getBody())
            .status(response.getStatusCodeValue())
            .build();
            
        responseDao.saveProviderRequestResponse(requestResponse);
    }

    /**
     * Validates the signature of a response
     * @return true if signature is valid, false otherwise
     */
    public boolean isResponseSignatureValid(String signature, Object data, String uuid, String method) {
        if (signature == null) {
            return false;
        }

        try {
            var jsonData = JsonUtils.toJsonNode(data);
            var serializedData = signatureCreator.serializeData(jsonData);
            var textToVerify = buildVerificationText(method, uuid, serializedData);
            
            log.debug("Verifying signature - Input: {}, Data: {}, Text: {}", 
                     signature, serializedData, textToVerify);

            if (signatureVerifier.verifySignature(signature, textToVerify)) {
                log.info("Signature validation successful for UUID: {}", uuid);
                return true;
            }
            
            log.warn("Signature validation failed for UUID: {}", uuid);
            return false;
            
        } catch (Exception e) {
            log.error("Failed to validate signature for UUID: " + uuid, e);
            return false;
        }
    }
    
    private String buildVerificationText(String method, String uuid, String serializedData) {
        return method + uuid + serializedData;
    }
}