package main.java.PaymentChecksHandler.ChecksHandler.models;
import lombok.Data;

@Data
public class PaymentDetails {
    private String method;                 
    private String type;                   
    private String totalAmount;           
    private String currencyCode;          
    private String transactionReference;  
    private String providerIdentifier;    
    private String creditorAccountNumber; 
    private String debitorAccountNumber;  
}
