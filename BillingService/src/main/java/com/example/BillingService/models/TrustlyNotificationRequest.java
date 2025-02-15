package com.example.BillingService.models;

import lombok.Data;

@Data
public class TrustlyNotificationRequest {
	private String paymentId;
	private String status;
	private String code;
	private String message;
}
