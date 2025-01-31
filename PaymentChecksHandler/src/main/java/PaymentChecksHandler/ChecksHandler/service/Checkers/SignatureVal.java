package main.java.PaymentChecksHandler.ChecksHandler.service.Checkers;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import main.java.PaymentChecksHandler.ChecksHandler.Constants.TransactionErrorCodes;
import main.java.PaymentChecksHandler.ChecksHandler.exceptions.ValidationException;
import main.java.PaymentChecksHandler.ChecksHandler.models.PaymentRequest;
import main.java.PaymentChecksHandler.ChecksHandler.service.Checkers;
import main.java.PaymentChecksHandler.ChecksHandler.utils.LogMessage;
import main.java.PaymentChecksHandler.ChecksHandler.utils;

@Component
public class SignatureVal implements Validator {

    private static final Logger LOGGER = LogManager.getLogger(SignatureValidator.class);

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Value("${payment.signatureKey}")
    private String signatureKey;

    @Autowired
    private HmacSha256 hmacSha256;

    @Override
    public void performValidation(PaymentRequest paymentRequest) {
        String signature = getSignatureFromRequest();
        validateSignature(signature, paymentRequest);
    }

    // Helper method to retrieve the signature from the request header
    private String getSignatureFromRequest() {
        String signature = httpServletRequest.getHeader("signature");
        if (signature == null) {
            LogMessage.log(LOGGER, "Signature not found");
            throw new ValidationException(HttpStatus.UNAUTHORIZED, ErrorCodeEnum.SIGNATURE_NOT_FOUND.getErrorCode(),
                    ErrorCodeEnum.SIGNATURE_NOT_FOUND.getErrorMessage());
        }
        return signature;
    }

    // Helper method to validate the signature
    private void validateSignature(String signature, PaymentRequest paymentRequest) {
        try {
            Gson gson = new Gson();
            byte[] signatureKeyBytes = signatureKey.getBytes();

            String messageDigest = hmacSha256.generateHmac256(gson.toJson(paymentRequest), signatureKeyBytes);

            if (!messageDigest.equals(signature)) {
                LogMessage.log(LOGGER, "Signature does not match. Payment request might be altered.");
                LogMessage.log(LOGGER, "MessageDigest: " + messageDigest + " , Signature: " + signature);

                throw new ValidationException(HttpStatus.UNAUTHORIZED, ErrorCodeEnum.SIGNATURE_ALTERED.getErrorCode(),
                        ErrorCodeEnum.SIGNATURE_ALTERED.getErrorMessage());
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            LogMessage.log(LOGGER, "Error occurred during HmacSHA256 calculation: " + e.getMessage());
            throw new ValidationException(HttpStatus.INTERNAL_SERVER_ERROR,
                    ErrorCodeEnum.EXCEPTION_IN_SIGNATURE_CALCULATION.getErrorCode(),
                    ErrorCodeEnum.EXCEPTION_IN_SIGNATURE_CALCULATION.getErrorMessage());
        }
    }
}
