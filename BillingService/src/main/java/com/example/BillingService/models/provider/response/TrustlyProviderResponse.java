package com.example.BillingService.models.provider.response;

import lombok.Data;

@Data
public class TrustlyProviderResponse {
	private String paymentId;
	private String redirectUrl;
}
