package com.coreprovider.coreprovider.service.impl;

import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.coreprovider.coreprovider.constants.Constants;
import com.coreprovider.coreprovider.constants.ErrorCodeEnum;
import com.coreprovider.coreprovider.exception.ValidationException;
import com.coreprovider.coreprovider.http.HttpRequest;
import com.coreprovider.coreprovider.http.HttpRestTemplateEngine;
import com.coreprovider.coreprovider.models.request.CoreTrustlyProvider;
import com.coreprovider.coreprovider.models.request.TrustlyNotificationRequest;
import com.coreprovider.coreprovider.models.response.ResponseData;
import com.coreprovider.coreprovider.models.response.Result;
import com.coreprovider.coreprovider.models.response.TrustlyCoreResponse;
import com.coreprovider.coreprovider.service.PaymentService;
import com.coreprovider.coreprovider.util.JsonUtils;
import com.coreprovider.coreprovider.util.LogMessage;
import com.coreprovider.coreprovider.util.SHA256RSASignatureVerifier;
import com.coreprovider.coreprovider.util.SignatureCreator;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.gson.Gson;

@Component
public class PaymentServiceImpl implements PaymentService {

	private static final Logger LOGGER = LogManager.getLogger(PaymentServiceImpl.class);

	private static final String SUCCESS = "SUCCESS";

	@Value("${trustly.initiate.payment.url}")
	private String initiatePaymentUrl;

	@Value("${cpt.notification.url}")
	private String notificationPaymentUrl;

	@Autowired
	private SignatureCreator signatureCreator;

	@Autowired
	private HttpRestTemplateEngine httpRestTemplateEngine;

	@Autowired
	private SHA256RSASignatureVerifier sigVerify;

	@Autowired
	private SignatureCreator sigCreator;

	@Autowired
	PaymentServiceHelper serviceHelper;

	@Override
	public TrustlyCoreResponse initiatePayment(CoreTrustlyProvider trustlyProviderRequest) {
		String requestUUID = trustlyProviderRequest.getParams().getUuid();

		boolean isSigValid = serviceHelper.isSignatureValid(trustlyProviderRequest, requestUUID);

		if (!isSigValid) {
			LogMessage.log(LOGGER, " INVALID signature for requestUUID:" + requestUUID);
			throw new ValidationException(HttpStatus.UNAUTHORIZED,
					ErrorCodeEnum.ERROR_UNABLE_TO_VERIFY_RSA_SIGNATURE.getErrorCode(),
					ErrorCodeEnum.ERROR_UNABLE_TO_VERIFY_RSA_SIGNATURE.getErrorMessage(),
					requestUUID,
					Constants.METHOD_DEPOSIT);
		}

		LogMessage.debug(LOGGER, " SIGNATURE VALID, continuing request processing");

		String paymentId = UUID.randomUUID().toString();

		ResponseData responseData = ResponseData.builder()
				.orderid(paymentId)
				.url(initiatePaymentUrl + paymentId)
				.build();

		String signature = serviceHelper.prepareSignature(requestUUID, responseData);

		Result result = Result.builder()
				.data(responseData)
				.method("Deposit")
				.uuid(requestUUID)
				.signature(signature)
				.build();

		TrustlyCoreResponse trustlyCoreResponse = TrustlyCoreResponse.builder().result(result).version("1.1").build();
		LogMessage.log(LOGGER, " TrustlyCoreResponse is -> " + trustlyCoreResponse);
		return trustlyCoreResponse;
	}

	@Override
	public void processPayment(String paymentId, String status) {

		TrustlyNotificationRequest trustlyNotificationRequest = TrustlyNotificationRequest.builder()
				.paymentId(paymentId).status(status).build();
		if (SUCCESS.equalsIgnoreCase(status)) {
			trustlyNotificationRequest.setCode("AC001");
			trustlyNotificationRequest.setMessage("Transaction succeded");
		} else {
			trustlyNotificationRequest.setCode("FL001");
			trustlyNotificationRequest.setMessage("Transaction Failed");
		}
		LogMessage.log(LOGGER, " trustlyNotificationRequest is -> " + trustlyNotificationRequest);

		Gson gson = new Gson();
		try {
			String signature = signatureCreator.generateSignature(gson.toJson(trustlyNotificationRequest));
			HttpHeaders headers = new HttpHeaders();
			headers.add("signature", signature);

			HttpRequest httpRequest = HttpRequest.builder().headers(headers)
					.request(gson.toJson(trustlyNotificationRequest)).httpMethod(HttpMethod.POST)
					.url(notificationPaymentUrl).build();

			httpRestTemplateEngine.execute(httpRequest);

		} catch (Exception e) {
			LogMessage.log(LOGGER, "EXception while creating signature ");
		}

	}

}
