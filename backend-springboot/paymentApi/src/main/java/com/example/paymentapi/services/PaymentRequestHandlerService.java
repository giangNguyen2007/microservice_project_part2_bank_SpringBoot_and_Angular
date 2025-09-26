package com.example.paymentapi.services;


import gng.learning.sharedLibrary.rabbitMqMessage.PaymentRequest;
import gng.learning.sharedLibrary.rabbitMqMessage.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentRequestHandlerService {

    private SimulatedPaymentProcessor paymentProcessor;
    private PaymentResponseDispatcher dispatcher;

    @Autowired
    public PaymentRequestHandlerService(SimulatedPaymentProcessor paymentProcessor, PaymentResponseDispatcher dispatcher) {
        this.paymentProcessor = paymentProcessor;
        this.dispatcher = dispatcher;
    }

    public void handlePaymentRequest(PaymentRequest paymentRequest) {
        System.out.println("[x] Received POJO: " + paymentRequest.getDescription());


        // treat payment requests
        boolean result = paymentProcessor.paymentResult(true);

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(" payment result = " + result);


        // send back payment response

        PaymentResponse response = new PaymentResponse();
        response.setPaymentId(UUID.randomUUID().toString());
        response.setTransactionId(paymentRequest.getTransactionId());
        response.setSuccess(result);

        dispatcher.publish(response);
    }
}
