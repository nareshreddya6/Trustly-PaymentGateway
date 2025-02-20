package com.example.ProviderTrustly.service;

import com.example.ProviderTrustly.http.HttpRequest;
import com.example.ProviderTrustly.pojo.request.TrustlyProviderRequest;

public interface RequestFormatter {

	HttpRequest prepareRequest(TrustlyProviderRequest trustlyProviderRequest);
}
