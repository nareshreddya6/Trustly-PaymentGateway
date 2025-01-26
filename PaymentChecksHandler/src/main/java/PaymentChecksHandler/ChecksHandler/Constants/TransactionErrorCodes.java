package main.java.PaymentChecksHandler.ChecksHandler.Constants;

// Define an enumeration to handle various error codes and messages
public enum TransactionErrorCodes {
    MISSING_SIGNATURE("ERR_10002", "Request is invalid: signature is missing."),
    GENERAL_ERROR("ERR_10001", "An unexpected error occurred. Please try again later."),
    ALTERED_SIGNATURE("ERR_10003", "Unauthorized: request data has been tampered with."),
    SIGNATURE_CALCULATION_ERROR("ERR_10004", "Error during signature calculation."),
    INVALID_AMOUNT("ERR_10005", "Invalid request: the amount parameter is empty or invalid."),
    INVALID_CURRENCY("ERR_10006", "Invalid request: the currency parameter is empty or invalid."),
    INVALID_EMAIL("ERR_10007", "Invalid request: the email parameter is empty or invalid."),
    INVALID_FIRST_NAME("ERR_10008", "Invalid request: the first name parameter is empty or invalid."),
    INVALID_LAST_NAME("ERR_10009", "Invalid request: the last name parameter is empty or invalid."),
    MISSING_TRANSACTION_REFERENCE("ERR_10010", "Invalid request: transaction reference is missing or invalid."),
    INVALID_PAYMENT_METHOD("ERR_10011", "Invalid request: the payment method parameter is invalid."),
    INVALID_PAYMENT_TYPE("ERR_10012", "Invalid request: the payment type parameter is invalid."),
    EMPTY_REQUEST("ERR_10013", "Invalid request: the payload is empty."),
    EMPTY_PAYMENT_OBJECT("ERR_10014", "Invalid request: the payment object is missing."),
    EMPTY_USER_OBJECT("ERR_10015", "Invalid request: the user object is missing."),
    INVALID_PHONE_NUMBER("ERR_10016", "Invalid request: the phone number parameter is invalid."),
    MISSING_PROVIDER_ID("ERR_10017", "Invalid request: the provider ID is missing."),
    DUPLICATE_TRANSACTION("ERR_10018", "Invalid request: duplicate transaction reference."),
    USER_CREATION_FAILED("ERR_10019", "Error: user creation process failed."),
    PAYMENT_CREATION_FAILED("ERR_10020", "Error: payment creation process failed."),
    INVALID_DEBIT_ACCOUNT("ERR_10021", "Invalid request: the debit account number is invalid."),
    INVALID_CREDIT_ACCOUNT("ERR_10022", "Invalid request: the credit account number is invalid.");

    // Private fields to store error code and error message
    private final String code;
    private final String message;

    // Constructor to initialize the error code and message
    TransactionErrorCodes(String code, String message) {
        this.code = code;
        this.message = message;
    }

    // Custom getter for the error code
    public String getCode() {
        return code;
    }

    // Custom getter for the error message
    public String getMessage() {
        return message;
    }
}
