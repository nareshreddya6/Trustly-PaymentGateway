package main.java.PaymentChecksHandler.ChecksHandler.models;
import lombok.Data;

@Data
public class PaymentDetails {
    private String method;                 // Renamed from 'paymentMethod'
    private String type;                   // Renamed from 'paymentType'
    private String totalAmount;            // Renamed from 'amount'
    private String currencyCode;           // Renamed from 'currency'
    private String transactionReference;   // Renamed from 'merchantTransactionReference'
    private String providerIdentifier;     // Renamed from 'providerId'
    private String creditorAccountNumber;  // Renamed from 'creditorAccount'
    private String debitorAccountNumber;   // Renamed from 'debitorAccount'
}
