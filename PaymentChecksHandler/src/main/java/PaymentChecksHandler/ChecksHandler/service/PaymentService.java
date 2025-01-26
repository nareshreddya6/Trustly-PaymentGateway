package main.java.PaymentChecksHandler.ChecksHandler.service;

import main.java.PaymentChecksHandler.ChecksHandler.Models.PaymentRequest;

public interface PaymentService {
    PaymentResponse CheckAndInitiatepay(PaymentRequest paymentreq);
}
