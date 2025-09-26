package com.example.paymentapi.config;


import com.example.paymentapi.services.PaymentRequestHandlerService;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqConfig {

    // ----- Core settings -----
    // bind from properties file
    @Value("${myapp.ampq.host}")
    private String HOST;

    @Value("${myapp.ampq.port}")
    private int PORT;
    public static final String USER  = "guest";
    public static final String PASS  = "guest";

    public static final String PAYMENTREQUEST_QUEUE = "paymentRequest.queue";

    public static final String EXCHANGE = "myExchange";

    public static final String PAYMENTRESPONSE_ROUTINGKEY = "paymentResponse.routingKey";

    public static final String PAYMENTREQUEST_ROUTINGKEY = "paymentRequest.routingKey";


    // Function : Connection to the broker
    @Bean
    public ConnectionFactory connectionFactory() {

        System.out.println("rabbitMq host and port =" + this.HOST + " : " + this.PORT);

        CachingConnectionFactory cf = new CachingConnectionFactory(this.HOST, this.PORT);
        cf.setUsername(this.USER);
        cf.setPassword(this.PASS);
        // cf.setVirtualHost("/"); // if needed
        return cf;
    }

    @Bean
    public TopicExchange exchange(){
        return ExchangeBuilder.topicExchange(this.EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue queue(){
        return QueueBuilder.durable(this.PAYMENTREQUEST_QUEUE).build();
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange){
        return BindingBuilder.bind(queue).to(exchange).with(this.PAYMENTREQUEST_ROUTINGKEY);
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




    // 3) => for message publishing
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        // rabbitTemplate.setMandatory(true); // to be notified of unroutable messages
        return rabbitTemplate;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(PaymentRequestHandlerService paymentRequestHandler, MessageConverter converter) {

        //set to call the method "handle" of the handler Object
        MessageListenerAdapter adapter = new MessageListenerAdapter(paymentRequestHandler, "handlePaymentRequest"); // calls handler.handle(...)

        // Adapter automatically infered the parameter type from handler.handle()'s input parameter
        // then pass it to the converter
        // => converter know to which type to convert Json to
        adapter.setMessageConverter(converter); // JSON <-> POJO
        return adapter;
    }

    // Container
    @Bean
    public SimpleMessageListenerContainer container(ConnectionFactory cf, MessageListenerAdapter messageListenerAdapter) {
        SimpleMessageListenerContainer c = new SimpleMessageListenerContainer(cf);

        // bind the listener to this queue
        c.setQueueNames(PAYMENTREQUEST_QUEUE);
        c.setMessageListener(messageListenerAdapter);
        return c;
    }


}
