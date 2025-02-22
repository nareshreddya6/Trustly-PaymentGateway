package com.example.ProviderTrustly.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrustlyProviderResponse {
	private String paymentId;
	private String redirectUrl;
}
