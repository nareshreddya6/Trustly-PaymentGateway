package main.java.PaymentChecksHandler.ChecksHandler.models;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
	private String paymentReference;
	private String redirectUrl;
}