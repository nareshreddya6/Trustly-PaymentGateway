package main.java.PaymentChecksHandler.ChecksHandler.controller;

// Import necessary classes for logging, HTTP handling, and Spring MVC annotations
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Import custom classes for the payment request, response, service, and constants
import main.java.PaymentChecksHandler.ChecksHandler.constants.Endpoint;
import main.java.PaymentChecksHandler.ChecksHandler.Models.PaymentRequest;
import main.java.PaymentChecksHandler.ChecksHandler.Models.PaymentResponse;
import main.java.PaymentChecksHandler.ChecksHandler.service.PaymentService;
import main.java.PaymentChecksHandler.ChecksHandler.LogMessage;






// Define the class as a REST controller, handling HTTP requests
@RestController
// Map the controller to a specific endpoint defined in the Endpoint class
@RequestMapping(Endpoint.VALIDATION_MAPPING)
public class PaymentChecksController {

    // Logger instance to log messages for debugging and tracking
    private static final Logger LOGGER = LogManager.getLogger(PaymentChecksController.class);

    // Automatically inject the PaymentService bean to handle business logic to be implemented 
    @Autowired
    private PaymentService PaymentService;

    // POST request to initiate a payment
    // The method will be accessed via the endpoint defined in Endpoint.INITIATE_PAYMENT
    @PostMapping(value = Endpoint.INITIATE_PAYMENT)
    public ResponseEntity<PaymentResponse> sale(@RequestBody PaymentRequest paymentRequest) {
        
        // Set a log message prefix to identify the log entries related to the payment initiation process
        LogMessage.setLogMessagePrefix("/INITIATE_PAYMENT");
        
        // Log the incoming payment request for debugging purposes
        LogMessage.log(LOGGER, " initiate payment request " + paymentRequest);

        // Call the paymentService to validate and initiate the payment and return a response
        // The response is wrapped in a ResponseEntity, and the HTTP status code is set to CREATED (201)
        //test
        //return new ResponseEntity<>("payment initiated", HttpStatus.CREATED);
    
        return new ResponseEntity<>(paymentService.CheckAndInitiatepay(paymentRequest), HttpStatus.CREATED);
    }
}

