package main.java.PaymentChecksHandler.ChecksHandler.http;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.*;

import com.cpt.payments.util.LogMessage;

@Component
public class HttpRestTemplateEngine {
    private static final Logger LOGGER = LogManager.getLogger(HttpRestTemplateEngine.class);

    public ResponseEntity<String> execute(HttpRequest httpRequest) {
        try {
            RestTemplate restTemplate = createRestTemplate();
            HttpEntity<?> request = buildHttpRequest(httpRequest);

            ResponseEntity<String> response = restTemplate.exchange(httpRequest.getUrl(),
                    prepareHttpMethod(httpRequest.getHttpMethod()), request, String.class);

            HttpStatus statusCode = response.getStatusCode();
            LogMessage.debug(LOGGER, "Received API response with statusCode:" + statusCode);

            return handleResponse(statusCode, response);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            LogMessage.log(LOGGER, "Exception occurred: " + e);
            return handleErrorResponse(e);
        } catch (Exception e) {
            LogMessage.logException(LOGGER, e);
            e.printStackTrace();
            return null;
        }
    }

    private RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setOutputStreaming(false);
        restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(requestFactory));

        return restTemplate;
    }

    private HttpEntity<?> buildHttpRequest(HttpRequest httpRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(httpRequest.getRequest(), headers);
    }

    private ResponseEntity<String> handleResponse(HttpStatus statusCode, ResponseEntity<String> response) {
        if (statusCode.is2xxSuccessful()) {
            return response;
        } else {
            String errorResponse = response.getBody();
            return createCustomErrorResponse(statusCode, errorResponse, response.getHeaders());
        }
    }

    private ResponseEntity<String> handleErrorResponse(HttpClientErrorException | HttpServerErrorException e) {
        return createCustomErrorResponse(e.getStatusCode(), e.getResponseBodyAsString(), e.getResponseHeaders());
    }

    private static ResponseEntity<String> createCustomErrorResponse(HttpStatus statusCode, String errorResponse,
            HttpHeaders httpHeaders) {
        if (httpHeaders == null) {
            httpHeaders = new HttpHeaders();
        }
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> response = new ResponseEntity<>(errorResponse, httpHeaders, statusCode);
        LogMessage.debug(LOGGER, "Error response created: " + response);
        return response;
    }

    private HttpMethod prepareHttpMethod(HttpMethod methodType) {
        switch (methodType) {
            case POST:
                return HttpMethod.POST;
            case GET:
                return HttpMethod.GET;
            case PATCH:
                return HttpMethod.PATCH;
            case PUT:
                return HttpMethod.PUT;
            default:
                LogMessage.log(LOGGER, "Unknown HTTP method, defaulting to POST");
                return HttpMethod.POST;
        }
    }
}
