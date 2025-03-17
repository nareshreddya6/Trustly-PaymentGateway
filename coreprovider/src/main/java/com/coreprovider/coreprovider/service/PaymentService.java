package com.coreprovider.coreprovider.service;

import com.coreprovider.coreprovider.models.request.CoreTrustlyProvider;
import com.coreprovider.coreprovider.models.request.TrustlyProviderRequest;
import com.coreprovider.coreprovider.models.response.TrustlyCoreResponse;
import com.coreprovider.coreprovider.models.response.TrustlyProviderResponse;

public interface PaymentService {

	TrustlyCoreResponse initiatePayment(CoreTrustlyProvider trustlyProviderRequest);

	void processPayment(String paymentId, String success);

}
