package gng.learning.accountapi.services.rabbitmq;

import gng.learning.accountapi.data.Transaction;
import gng.learning.accountapi.properties.AmqpProperties;
import gng.learning.sharedLibrary.rabbitMqMessage.PaymentRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentRequestDispatcher {

    Logger logger = LoggerFactory.getLogger(PaymentRequestDispatcher.class);

    private RabbitTemplate _rabbitTemplate;
    private AmqpProperties _amqpProperties;


    public PaymentRequestDispatcher(RabbitTemplate rabbitTemplate, AmqpProperties amqpProperties) {
        _rabbitTemplate = rabbitTemplate;
        _amqpProperties = amqpProperties;
    }

    public void dispatchPaymentRequest(Transaction newTransaction){

        PaymentRequest paymentRequest = new PaymentRequest(
               newTransaction.getAccount().getUserId().toString() ,
                newTransaction.getAccount().getId().toString(),
                newTransaction.getId().toString(),
                newTransaction.getAmount(),
                newTransaction.getDescription()
        );

        try{
            _rabbitTemplate.convertAndSend(_amqpProperties.getExchange(), _amqpProperties.getPaymentRequestRoutingKey(), paymentRequest);

            logger.debug("Successfully published paymentRequest for transaction: {}", newTransaction.getId());


        } catch ( AmqpException e) {


            logger.error("Publication failed for transaction: {} with error: {}", newTransaction.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to publish payment request for transaction: " + newTransaction.getId(), e);

        }

    }
}
