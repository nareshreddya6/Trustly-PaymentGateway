package test.java.PaymentChecksHandler.ChecksHandler;

import main.java.PaymentChecksHandler.ChecksHandler.models.Payment;
import main.java.PaymentChecksHandler.ChecksHandler.models.PaymentRequest;
import main.java.PaymentChecksHandler.ChecksHandler.models.User;
import main.java.PaymentChecksHandler.ChecksHandler.util.HmacSha256;
import com.google.gson.Gson;

public class TestDataProviderUtil {

    public static User getTestUserBean() {
        User user = new User();
        user.setEmail("sarah.jones@example.com");
        user.setFirstName("Sarah");
        user.setLastName("Jones");
        user.setPhoneNumber("9473829182");
        return user;
    }

    public static com.cpt.payments.dto.User getTestUserDto() {
        com.cpt.payments.dto.User user = new com.cpt.payments.dto.User();
        user.setId(456L);
        user.setEmail("sarah.jones@example.com");
        user.setFirstName("Sarah");
        user.setLastName("Jones");
        user.setPhoneNumber("9473829182");
        return user;
    }

    public static Payment getTestPayment() {
        Payment payment = new Payment();
        payment.setAmount("50.0");
        payment.setCreditorAccount("5566778899001122");
        payment.setCurrency("GBP");
        payment.setDebitorAccount("9988776655443322");
        payment.setMerchantTransactionReference("payment_test5");
        payment.setPaymentMethod("Credit Card");
        payment.setPaymentType("REFUND");
        payment.setProviderId("VISA");
        return payment;
    }
}
