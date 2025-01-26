package main.java.PaymentChecksHandler.ChecksHandler.models;

import lombok.Data;

@Data
public class PaymentRequest {
	private User user;
	private PaymentDetails paymentdetails;
}
