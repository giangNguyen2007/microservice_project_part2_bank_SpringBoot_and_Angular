package gng.learning.accountapi.services.amqp;

import gng.learning.accountapi.data.Account;
import gng.learning.accountapi.data.AccountType;
import gng.learning.accountapi.data.Transaction;
import gng.learning.accountapi.properties.AmqpProperties;
import gng.learning.accountapi.services.rabbitmq.PaymentRequestDispatcher;
import gng.learning.accountapi.services.rabbitmq.PaymentResponseHandlerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;


// send request directly rabbitMq
// with PaymentAPI listenning for new message
// manual check

@SpringBootTest(
    classes = {

            PaymentResponseHandlerService.class,
            AmqpProperties.class,
            PaymentRequestDispatcher.class
    }
)
class PaymentRequestDispatcherTest {

    @Autowired
    PaymentRequestDispatcher _dispatcher;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void dispatchPaymentRequest() {

        Account myAccount = new Account(UUID.randomUUID(), AccountType.COMPTE_COURANT);
        myAccount.setId(UUID.randomUUID());

        Transaction newTransaction = new Transaction();
        newTransaction.setAccount(myAccount);
        newTransaction.setAmount(100);
        newTransaction.setId(UUID.randomUUID());
        newTransaction.setDescription("payment for my trip to Disneyland");

        _dispatcher.dispatchPaymentRequest(newTransaction);
    }
}