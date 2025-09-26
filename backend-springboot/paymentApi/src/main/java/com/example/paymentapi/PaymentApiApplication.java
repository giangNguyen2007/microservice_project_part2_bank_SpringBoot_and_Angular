package com.example.paymentapi;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PaymentApiApplication {

    public static void main(String[] args) {

        SpringApplication.run(PaymentApiApplication.class, args);
    }

}
