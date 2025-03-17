package com.coreprovider.coreprovider.util;

import com.coreprovider.coreprovider.models.request.Attributes;
import com.coreprovider.coreprovider.models.request.Data;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Component
public class SignatureCreator {
    private static final Logger logger = LogManager.getLogger(SignatureCreator.class);
    private static final Path PRIVATE_KEY_LOCATION = Paths.get("./src/main/resources/private.pem");
    private static final String RSA_ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA256withRSA";

    /**
     * Converts a JsonNode into a deterministic string representation.
     * Handles objects, arrays, and primitive values while maintaining consistent
     * ordering.
     */
    public String createOrderedDataString(JsonNode node) {
        if (node == null) {
            return "";
        }

        StringBuilder result = new StringBuilder();

        if (node.isObject()) {
            processObjectNode((ObjectNode) node, result);
        } else if (node.isArray()) {
            processArrayNode(node, result);
        } else {
            result.append(node.asText());
        }

        return result.toString();
    }

    private void processObjectNode(ObjectNode objNode, StringBuilder result) {
        getSortedFieldStream(objNode)
                .forEach(fieldName -> {
                    JsonNode fieldValue = objNode.get(fieldName);
                    result.append(fieldName);
                    result.append(createOrderedDataString(fieldValue));
                });
    }

    private void processArrayNode(JsonNode arrayNode, StringBuilder result) {
        arrayNode.elements().forEachRemaining(
                element -> result.append(createOrderedDataString(element)));
    }

    private Stream<String> getSortedFieldStream(ObjectNode node) {
        return StreamSupport
                .stream(Spliterators.spliteratorUnknownSize(node.fieldNames(), 0), false)
                .sorted();
    }

    /**
     * Loads and returns the private key from the configured location.
     * Supports both PKCS#8 and traditional PEM formats.
     */
    public PrivateKey loadPrivateKey() throws IOException {
        logger.debug("Loading private key from: {}", PRIVATE_KEY_LOCATION);

        String keyContent = Files.readString(PRIVATE_KEY_LOCATION);
        logger.info("Successfully read private key file");

        try {
            return keyContent.contains("-----BEGIN PRIVATE KEY-----")
                    ? loadPKCS8PrivateKey(keyContent)
                    : loadTraditionalPrivateKey();
        } catch (Exception e) {
            logger.error("Failed to load private key", e);
            throw new IOException("Private key loading failed", e);
        }
    }

    private PrivateKey loadPKCS8PrivateKey(String keyContent) throws GeneralSecurityException {
        logger.debug("Processing PKCS#8 format private key");

        String cleanKey = keyContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(cleanKey);
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);

        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
    }

    private PrivateKey loadTraditionalPrivateKey() throws Exception {
        logger.debug("Processing traditional PEM format private key");

        Security.addProvider(new BouncyCastleProvider());
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);

        try (FileReader keyReader = new FileReader(PRIVATE_KEY_LOCATION.toFile());
                PemReader pemReader = new PemReader(keyReader)) {

            PemObject pemObject = pemReader.readPemObject();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pemObject.getContent());

            return keyFactory.generatePrivate(keySpec);
        }
    }

    /**
     * Creates a digital signature for the provided data using SHA256withRSA.
     */
    public String sign(String data) throws Exception {
        PrivateKey key = loadPrivateKey();
        Signature signer = Signature.getInstance(SIGNATURE_ALGORITHM);

        signer.initSign(key);
        signer.update(data.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(signer.sign());
    }

    // Demo implementation
    public static void main(String[] args) throws Exception {
        SignatureCreator signer = new SignatureCreator();

        // Sample payment data setup
        Attributes paymentAttrs = Attributes.builder()
                .country("LT")
                .locale("en")
                .currency("EUR")
                .amount(18.10)
                .firstname("john")
                .lastname("peter")
                .email("johnpeter@gmail.com")
                .failURL("https://somedomain.com/failure/trustly/ref1")
                .successURL("https://somedomain.com/success/trustly/ref1")
                .build();

        Data paymentData = Data.builder()
                .username("CTPuser")
                .password("CTPpassword")
                .notificationURL("https://somedomain.com/trustly/notify/ref1")
                .endUserID("user1-id")
                .messageID("msg1-id")
                .attributes(paymentAttrs)
                .build();

        // Create and verify signature
        String method = "Deposit";
        String transactionId = "67d6c2f3-51b3-4eed-ad1a-16b4c4063c33";

        JsonNode dataNode = JsonUtils.toJsonNode(paymentData);
        String serializedData = signer.createOrderedDataString(dataNode);
        String dataToSign = method + transactionId + serializedData;

        String signature = signer.sign(dataToSign);

        // Verify the signature
        SHA256RSASignatureVerifier verifier = new SHA256RSASignatureVerifier();
        boolean isValid = verifier.verifySignature(signature, dataToSign);

        System.out.printf("Signature: %s%nVerification result: %b%n", signature, isValid);
    }
}