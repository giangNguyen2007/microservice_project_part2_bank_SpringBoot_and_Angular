package gng.learning.accountapi.services.rabbitmq;

import gng.learning.accountapi.services.TransactionService;
import gng.learning.sharedLibrary.rabbitMqMessage.PaymentResponse;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.logging.Logger;

@Service
public class PaymentResponseHandlerService {

    Logger logger = Logger.getLogger(PaymentResponseHandlerService.class.getName());

    private final TransactionService _transactionService;

    public PaymentResponseHandlerService(TransactionService transactionService) {
        _transactionService = transactionService;
    }

    public void handlePaymentResponse(PaymentResponse rsp) {

        logger.info("Handling payment response for transaction ID: " + rsp.getTransactionId() + " with status: " + rsp.getSuccess());


        if (rsp.getSuccess()) {
          
            // update transaction status and account balance
            _transactionService.confirmTransactionSuccess(  UUID.fromString(rsp.getTransactionId())  );

        } else {
        
            // update transaction status and account balance
            _transactionService.confirmTransactionFailure(  UUID.fromString(rsp.getTransactionId())  );

        }
    }
}
