package com.example.paymentapi.services;

import com.example.paymentapi.config.RabbitmqConfig;
import gng.learning.sharedLibrary.rabbitMqMessage.PaymentResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentResponseDispatcher {


    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public PaymentResponseDispatcher(RabbitTemplate template) {

        this.rabbitTemplate = template;
    }

    public void publish(PaymentResponse paymentResponse){

        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE, RabbitmqConfig.PAYMENTRESPONSE_ROUTINGKEY, paymentResponse);
    }


}
