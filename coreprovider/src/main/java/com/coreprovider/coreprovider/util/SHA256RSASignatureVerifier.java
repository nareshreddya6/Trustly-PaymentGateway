package com.coreprovider.coreprovider.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class SHA256RSASignatureVerifier {
    private static final Logger logger = Logger.getLogger(SHA256RSASignatureVerifier.class.getName());

    // Store the initialized public key
    private static PublicKey cachedPublicKey;

    @Value("${security.trustly.public.key}")
    private String trustlyPublicKeyContent;

    @PostConstruct
    private void initialize() {
        try {
            cachedPublicKey = initializePublicKey();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize public key", e);
            throw new RuntimeException("Security initialization failed", e);
        }
    }

    /**
     * Validates the digital signature of a request
     *
     * @param digitalSignature Base64 encoded signature
     * @param payload          The original request payload
     * @return true if signature is valid, false otherwise
     */
    public boolean validateSignature(String digitalSignature, String payload) {
        try {
            if (digitalSignature == null || payload == null) {
                logger.warning("Invalid input: signature or payload is null");
                return false;
            }

            Signature signatureValidator = Signature.getInstance("SHA256withRSA");
            signatureValidator.initVerify(cachedPublicKey);
            signatureValidator.update(payload.getBytes(StandardCharsets.UTF_8));

            byte[] decodedSignature = Base64.getDecoder().decode(digitalSignature);
            return signatureValidator.verify(decodedSignature);

        } catch (Exception e) {
            logger.log(Level.WARNING, "Signature validation failed", e);
            return false;
        }
    }

    private PublicKey initializePublicKey() throws Exception {
        String cleanKey = trustlyPublicKeyContent
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
    }
}