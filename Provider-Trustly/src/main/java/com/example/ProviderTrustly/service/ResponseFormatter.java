package com.example.ProviderTrustly.service;

import org.springframework.http.ResponseEntity;

import com.example.ProviderTrustly.pojo.response.TrustlyProviderResponse;

public interface ResponseFormatter {

	TrustlyProviderResponse processResponse(ResponseEntity<String> providerResponse);
}
