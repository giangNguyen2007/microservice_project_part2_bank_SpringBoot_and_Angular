package com.example.paymentapi.services;

import com.example.paymentapi.config.RabbitmqConfig;
import com.rabbitmq.client.Channel;
import gng.learning.sharedLibrary.rabbitMqMessage.PaymentRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.test.TestRabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.UUID;


// test the listenner by mocking RabbitMq connection
@SpringBootTest
//@Import(PaymentRequestHandlerServiceTest.TestConfig.class)
class PaymentRequestHandlerServiceTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        @Primary  // => assure to be used as highest priority in test
        ConnectionFactory mockConnectionFactory(){
            ConnectionFactory cf = Mockito.mock(ConnectionFactory.class);

            Connection mockConnection = Mockito.mock(Connection.class);

            Channel mockChannel = Mockito.mock(Channel.class);

            // set the behaviour of mocked object
            // Having to mock those methods because they will be called by the ListenerContainer to establish connection to RabbitMq
            Mockito.when(cf.createConnection()).thenReturn(mockConnection);
            Mockito.when(mockConnection.createChannel(Mockito.anyBoolean())).thenReturn(mockChannel);
            Mockito.when(mockChannel.isOpen()).thenReturn(true);

            return cf;
        }

        // Prevent topology declarations during tests
        @Bean
        //@Primary
        RabbitAdmin rabbitAdmin(ConnectionFactory cf) {
            RabbitAdmin admin = new RabbitAdmin(cf);
            admin.setAutoStartup(false);
            return admin;
        }


        @Bean
        //@Primary
        // from rabbitmq test package
        // publish directly to the listener, without going through the real RabbitMq app
        TestRabbitTemplate createPublisher(ConnectionFactory cf){
            return new TestRabbitTemplate(cf);
        }



    }

    @Autowired
    TestRabbitTemplate _testRabbitTemplate;

    @Autowired
    Jackson2JsonMessageConverter _converter;

    @MockitoBean
    PaymentRequestHandlerService _mockHandler;


    @Test
    void handlePaymentRequest() {

        _testRabbitTemplate.setMessageConverter(_converter);

        PaymentRequest sentPayment = new PaymentRequest(
                UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),100, "test payment request"
        );
        _testRabbitTemplate.convertAndSend(RabbitmqConfig.PAYMENTREQUEST_QUEUE, sentPayment );

        // verify number of times the handling method is called
        Mockito.verify(_mockHandler, Mockito.times(1)).handlePaymentRequest(Mockito.any(PaymentRequest.class));

    }
}