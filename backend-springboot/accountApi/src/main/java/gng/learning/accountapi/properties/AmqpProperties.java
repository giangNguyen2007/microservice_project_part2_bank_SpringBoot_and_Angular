package gng.learning.accountapi.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "myapp.ampq")
public class AmqpProperties {

    private String host;
    private Integer port;

    private String user;

    private String password;

    private String paymentResponseQueue;
    private String exchange;
    private String paymentResponseRoutingKey;
    private String paymentRequestRoutingKey;



    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getPaymentResponseQueue() {
            return paymentResponseQueue;
    }

    public void setPaymentResponseQueue(String paymentResponseQueue) {
        this.paymentResponseQueue = paymentResponseQueue;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getPaymentResponseRoutingKey() {
        return paymentResponseRoutingKey;
    }

    public void setPaymentResponseRoutingKey(String paymentResponseRoutingKey) {
        this.paymentResponseRoutingKey = paymentResponseRoutingKey;
    }

    public String getPaymentRequestRoutingKey() {
        return paymentRequestRoutingKey;
    }

    public void setPaymentRequestRoutingKey(String paymentRequestRoutingKey) {
        this.paymentRequestRoutingKey = paymentRequestRoutingKey;
    }
}
