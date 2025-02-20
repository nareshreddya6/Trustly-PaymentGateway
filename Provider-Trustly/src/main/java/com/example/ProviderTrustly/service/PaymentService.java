package com.example.ProviderTrustly.service;

import com.example.ProviderTrustly.pojo.request.TrustlyProviderRequest;
import com.example.ProviderTrustly.pojo.response.TrustlyProviderResponse;

public interface PaymentService {

	TrustlyProviderResponse initiatePayment(TrustlyProviderRequest trustlyProviderRequest);

}
