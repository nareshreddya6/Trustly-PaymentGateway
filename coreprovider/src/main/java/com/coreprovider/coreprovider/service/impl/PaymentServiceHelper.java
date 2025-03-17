package com.coreprovider.coreprovider.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.coreprovider.coreprovider.constants.Constants;
import com.coreprovider.coreprovider.constants.ErrorCodeEnum;
import com.coreprovider.coreprovider.exception.ValidationException;
import com.coreprovider.coreprovider.models.request.CoreTrustlyProvider;
import com.coreprovider.coreprovider.models.response.ResponseData;
import com.coreprovider.coreprovider.util.JsonUtils;
import com.coreprovider.coreprovider.util.LogMessage;
import com.coreprovider.coreprovider.util.SHA256RSASignatureVerifier;
import com.coreprovider.coreprovider.util.SignatureCreator;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class PaymentServiceHelper {

	private static final Logger LOGGER = LogManager.getLogger(PaymentServiceHelper.class);

	@Autowired
	private SignatureCreator sigCreator;

	@Autowired
	private SHA256RSASignatureVerifier sigVerify;

	public String prepareSignature(String requestUUID, ResponseData responseData) {
		String signature = null;
		try {
			String serializedData = sigCreator.serializeData(JsonUtils.toJsonNode(responseData));
			String plainText = Constants.METHOD_DEPOSIT + requestUUID + serializedData;
			signature = sigCreator.generateSignature(plainText);
			LogMessage.log(LOGGER, "Generating Signature while returning Trusly response::"
					+ "serializedData:" + serializedData + "|plainText:" + plainText + "|signature:" + signature);
		} catch (Exception e) {
			LogMessage.log(LOGGER, "Exception processing");
			LogMessage.logException(LOGGER, e);

			throw new ValidationException(HttpStatus.INTERNAL_SERVER_ERROR,
					ErrorCodeEnum.GENERIC_EXCEPTION.getErrorCode(),
					ErrorCodeEnum.GENERIC_EXCEPTION.getErrorMessage(),
					requestUUID,
					Constants.METHOD_DEPOSIT);
		}
		return signature;
	}

	public boolean isSignatureValid(CoreTrustlyProvider trustlyProviderRequest, String requestUUID) {
		String inputSignature = trustlyProviderRequest.getParams().getSignature();

		if (null == inputSignature) {
			LogMessage.log(LOGGER, " Input signature NULL for requestUUID:" + requestUUID);
			return false;
		}

		try {
			JsonNode jsonNode = JsonUtils.toJsonNode(trustlyProviderRequest.getParams().getData());
			String plainText = Constants.METHOD_DEPOSIT
					+ "" + trustlyProviderRequest.getParams().getUuid()
					+ sigCreator.serializeData(jsonNode);

			if (sigVerify.verifySignature(inputSignature, plainText)) {
				LogMessage.log(LOGGER,
						" Signature Valid|inputSignature:" + inputSignature + "|requestUUID:" + requestUUID);
				return true;
			}
		} catch (Exception e) {
			LogMessage.log(LOGGER, " Exception while validating signature::requestUUID:" + requestUUID);
			LogMessage.logException(LOGGER, e);
		}

		return false;
	}

}
