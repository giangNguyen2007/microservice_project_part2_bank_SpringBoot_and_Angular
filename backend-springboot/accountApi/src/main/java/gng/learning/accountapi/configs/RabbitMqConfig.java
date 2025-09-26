package gng.learning.accountapi.configs;

import gng.learning.accountapi.properties.AmqpProperties;
import gng.learning.accountapi.services.rabbitmq.PaymentResponseHandlerService;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
//@EnableConfigurationProperties(AmqpProperties.class)
public class RabbitMqConfig {

//    public static final String HOST  = "localhost";
//    public static final int    PORT  = 5672;
//    public static final String USER  = "guest";
//    public static final String PASS  = "guest";

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(RabbitMqConfig.class);


    private final AmqpProperties amqpProperties;

    public RabbitMqConfig(AmqpProperties amqpProperties) {
        this.amqpProperties = amqpProperties;
    }
//    public static final String PAYMENTRESPONSE_QUEUE = "paymentResponse.queue";
//
//    public static final String EXCHANGE = "myExchange";
//
//    public static final String PAYMENTRESPONSE_ROUTINGKEY = "paymentResponse.routingKey";
//
//    public static final String PAYMENTREQUEST_ROUTINGKEY = "paymentRequest.routingKey";


    // Function : Connection to the broker
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cf = new CachingConnectionFactory(amqpProperties.getHost(), amqpProperties.getPort());
        cf.setUsername(amqpProperties.getUser());
        cf.setPassword(amqpProperties.getPassword());

        logger.debug("RabbitMQ connection factory created with host: {}, port: {}, user: {}",
                     amqpProperties.getHost(), amqpProperties.getPort(), amqpProperties.getUser());
        // cf.setVirtualHost("/"); // if needed
        return cf;
    }

    @Bean
    public TopicExchange exchange(){
        return ExchangeBuilder.topicExchange(amqpProperties.getExchange()).durable(true).build();
    }

    @Bean
    public Queue queue(){
        return QueueBuilder.durable(amqpProperties.getPaymentResponseQueue()).build();
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(amqpProperties.getPaymentResponseRoutingKey());
    }

    // class for creating exchange, queue, binding
    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory cf) {
        RabbitAdmin admin =  new RabbitAdmin(cf);
        return admin;
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }



    // 3) => for message publishing, queue name is defined when calling convertAndSend()
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        // rabbitTemplate.setMandatory(true); // to be notified of unroutable messages
        return rabbitTemplate;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(PaymentResponseHandlerService handler, MessageConverter converter) {

        //set to call the method "handle" of the handler Object
        MessageListenerAdapter adapter = new MessageListenerAdapter(handler, "handlePaymentResponse"); // calls handler.handle(...)

        // Adapter automatically infered the parameter type from handler.handle()'s input parameter
        // then pass it to the converter
        // => converter know to which type to convert Json to
        adapter.setMessageConverter(converter); // JSON <-> POJO
        return adapter;
    }

    // Container => listen to queue PaymentResponseQueue
    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory cf, MessageListenerAdapter messageListenerAdapter) {
        SimpleMessageListenerContainer c = new SimpleMessageListenerContainer(cf);

        // bind the listener to this queue
        c.setQueueNames(amqpProperties.getPaymentResponseQueue());
        c.setMessageListener(messageListenerAdapter);
        return c;
    }

}
