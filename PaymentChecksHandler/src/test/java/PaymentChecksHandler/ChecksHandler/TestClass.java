package test.java.PaymentChecksHandler.ChecksHandler;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import main.java.PaymentChecksHandler.ChecksHandler.models.Payment;
import main.java.PaymentChecksHandler.ChecksHandler.models.PaymentRequest;
import main.java.PaymentChecksHandler.ChecksHandler.models.User;
import main.java.PaymentChecksHandler.ChecksHandler.util.HmacSha256;
import com.google.gson.Gson;

public class TestClass {

    public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException {
        User user = new User();
        user.setEmail("jaime@gmail.com");
        user.setFirstName("John");
        user.setLastName("snow");
        user.setPhoneNumber("+91099348535");

        user.setEmail("jaime1@gmail.com");
        user.setFirstName("John1");
        user.setLastName("snow1");
        user.setPhoneNumber("+91099348536");

        Payment payments = new Payment();
        payments.setAmount("34.00");
        payments.setCreditorAccount("1212121212121212");
        payments.setCurrency("EUR");
        payments.setDebitorAccount("4131516171771788");
        payments.setMerchantTransactionReference("test8");
        payments.setPaymentMethod("APM");
        payments.setPaymentType("SALE");
        payments.setProviderId("Trustly");

        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setPayment(payments);
        paymentRequest.setUser(user);

        HmacSha256 signatureGenerator = new HmacSha256();

        Gson gson = new Gson();
        String reqestJSON = gson.toJson(paymentRequest);
        System.out.println(reqestJSON);

        String signature = signatureGenerator.generateHmac256(reqestJSON, "TrustlyProject".getBytes());

        System.out.println(signature);

    }

}
