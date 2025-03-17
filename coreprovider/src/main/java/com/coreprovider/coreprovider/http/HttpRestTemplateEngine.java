package com.coreprovider.coreprovider.http;

import com.cpt.payments.util.LogMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Component
public class HttpRestTemplateEngine {
	private static final Logger logger = LogManager.getLogger(HttpRestTemplateEngine.class);
	private static final int DEFAULT_TIMEOUT = 30000; // 30 seconds

	public ResponseEntity<String> sendRequest(HttpRequest request) {
		try {
			RestTemplate client = setupRestTemplate();
			HttpEntity<?> httpEntity = buildHttpEntity(request);
			HttpMethod method = resolveHttpMethod(request.getHttpMethod());

			ResponseEntity<String> response = client.exchange(
					request.getUrl(),
					method,
					httpEntity,
					String.class);

			logger.debug("Response received with status: {}", response.getStatusCode());

			return handleResponse(response);
		} catch (HttpClientErrorException | HttpServerErrorException ex) {
			logger.error("HTTP error occurred: {}", ex.getMessage());
			return buildErrorResponse(ex.getStatusCode(), ex.getResponseBodyAsString(), ex.getResponseHeaders());
		} catch (Exception ex) {
			logger.error("Unexpected error during HTTP request", ex);
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occurred");
		}
	}

	private RestTemplate setupRestTemplate() {
		RestTemplate client = new RestTemplate();

		// Configure UTF-8 message converter
		client.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

		// Setup request factory with buffering
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setOutputStreaming(false);
		factory.setConnectTimeout(DEFAULT_TIMEOUT);
		factory.setReadTimeout(DEFAULT_TIMEOUT);

		client.setRequestFactory(new BufferingClientHttpRequestFactory(factory));

		return client;
	}

	private HttpEntity<?> buildHttpEntity(HttpRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		if (request.getHeaders() != null) {
			headers.addAll(request.getHeaders());
		}

		return new HttpEntity<>(request.getRequest(), headers);
	}

	private ResponseEntity<String> handleResponse(ResponseEntity<String> response) {
		if (response.getStatusCode().is2xxSuccessful()) {
			return response;
		}

		return buildErrorResponse(
				response.getStatusCode(),
				response.getBody(),
				response.getHeaders());
	}

	private ResponseEntity<String> buildErrorResponse(
			HttpStatus status,
			String errorMessage,
			HttpHeaders headers) {

		HttpHeaders responseHeaders = headers != null ? headers : new HttpHeaders();
		responseHeaders.setContentType(MediaType.APPLICATION_JSON);

		ResponseEntity<String> errorResponse = new ResponseEntity<>(
				errorMessage,
				responseHeaders,
				status);

		logger.debug("Error response created: {}", errorResponse);
		return errorResponse;
	}

	private HttpMethod resolveHttpMethod(HttpMethod requestMethod) {
		if (requestMethod == null) {
			logger.warn("No HTTP method specified, defaulting to POST");
			return HttpMethod.POST;
		}

		switch (requestMethod) {

			case POST:
				return HttpMethod.POST;
			case GET:
				return HttpMethod.GET;
			case PATCH:
				return HttpMethod.PATCH;
			case PUT:
				return HttpMethod.PUT;
			default:
				logger.warn("Unsupported HTTP method: {}, defaulting to POST", requestMethod);
				return HttpMethod.POST;
		}
	}
}