package com.example.BillingService.http;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.example.BillingService.util.LogMessage;

/**
 * Service component for handling HTTP requests using Spring's RestTemplate
 */
@Component
public class HttpRestTemplateEngine {
    // Logger instance for this class
    private static final Logger LOG = LogManager.getLogger(HttpRestTemplateEngine.class);
    
    // Default connection timeout in milliseconds
    private static final int DEFAULT_TIMEOUT = 30000;

    /**
     * Executes an HTTP request and returns the response
     * 
     * @param httpRequest The request to execute
     * @return ResponseEntity containing the response
     */
    public ResponseEntity<String> execute(HttpRequest httpRequest) {
        LOG.debug("Executing request to: {}", httpRequest.getUrl());
        
        try {
            // Configure and prepare RestTemplate
            RestTemplate client = configureRestTemplate();
            
            // Prepare headers and request entity
            HttpEntity<?> requestEntity = buildRequestEntity(httpRequest);
            
            // Determine HTTP method
            HttpMethod method = resolveHttpMethod(httpRequest.getHttpMethod());
            
            // Execute the request
            ResponseEntity<String> response = client.exchange(
                    httpRequest.getUrl(), 
                    method, 
                    requestEntity, 
                    String.class);
            
            // Process the response
            return handleResponse(response);
            
        } catch (HttpClientErrorException | HttpServerErrorException ex) {
            // Handle HTTP error responses (4xx, 5xx)
            LogMessage.log(LOG, "HTTP error occurred: " + ex.getStatusCode() + " - " + ex.getMessage());
            return wrapErrorResponse(ex.getStatusCode(), ex.getResponseBodyAsString(), ex.getResponseHeaders());
            
        } catch (Exception ex) {
            // Handle unexpected errors
            LogMessage.logException(LOG, ex);
            ex.printStackTrace();
            return null;
        }
    }
    
    /**
     * Creates a properly configured RestTemplate instance
     */
    private RestTemplate configureRestTemplate() {
        RestTemplate client = new RestTemplate();
        
        // Configure character encoding
        client.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
        
        // Configure request factory with buffering for logging
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setOutputStreaming(false);
        factory.setConnectTimeout(DEFAULT_TIMEOUT);
        factory.setReadTimeout(DEFAULT_TIMEOUT);
        
        client.setRequestFactory(new BufferingClientHttpRequestFactory(factory));
        
        return client;
    }
    
    /**
     * Builds the HTTP entity with headers and body
     */
    private HttpEntity<?> buildRequestEntity(HttpRequest httpRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        
        return new HttpEntity<>(httpRequest.getRequest(), headers);
    }
    
    /**
     * Processes the HTTP response
     */
    private ResponseEntity<String> handleResponse(ResponseEntity<String> response) {
        HttpStatus status = response.getStatusCode();
        LogMessage.debug(LOG, "Received response with status: " + status);
        
        if (status.is2xxSuccessful()) {
            return response;
        } else {
            return wrapErrorResponse(status, response.getBody(), response.getHeaders());
        }
    }
    
    /**
     * Creates a standardized error response
     */
    private ResponseEntity<String> wrapErrorResponse(
            HttpStatus status, String errorBody, HttpHeaders headers) {
        
        HttpHeaders responseHeaders = (headers != null) ? headers : new HttpHeaders();
        responseHeaders.setContentType(MediaType.APPLICATION_JSON);
        
        ResponseEntity<String> errorResponse = new ResponseEntity<>(errorBody, responseHeaders, status);
        LogMessage.debug(LOG, "Created error response: " + status);
        
        return errorResponse;
    }
    
    /**
     * Maps the requested HTTP method or defaults to POST
     */
    private HttpMethod resolveHttpMethod(HttpMethod requestedMethod) {
        if (requestedMethod == null) {
            LogMessage.log(LOG, "No HTTP method specified, defaulting to POST");
            return HttpMethod.POST;
        }
        
        switch (requestedMethod) {
            case GET:
                return HttpMethod.GET;
            case POST:
                return HttpMethod.POST;
            case PUT:
                return HttpMethod.PUT;
            case PATCH:
                return HttpMethod.PATCH;
            case DELETE:
                return HttpMethod.DELETE;
            default:
                LogMessage.log(LOG, "Unsupported method: " + requestedMethod + ", defaulting to POST");
                return HttpMethod.POST;
        }
    }
}