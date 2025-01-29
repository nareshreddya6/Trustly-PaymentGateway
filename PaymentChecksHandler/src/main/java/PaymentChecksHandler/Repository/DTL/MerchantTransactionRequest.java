
package main.java.PaymentChecksHandler.Repository.DTL;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MerchantTransactionRequest {
    private String merchantTransactionReference;
    private String transactionRequest;
}
