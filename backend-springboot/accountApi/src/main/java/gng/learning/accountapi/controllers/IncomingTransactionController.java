package gng.learning.accountapi.controllers;

import gng.learning.accountapi.customException.InsufficientFundException;
import gng.learning.accountapi.customException.ItemNotFoundException;
import gng.learning.accountapi.data.IncomingDebitTransaction;
import gng.learning.accountapi.services.AccountService;
import gng.learning.accountapi.services.IncomingTransactionService;
import gng.learning.accountapi.services.rabbitmq.PaymentRequestDispatcher;
import gng.learning.sharedLibrary.dtos.accountController.NewIncomingTransactionDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping(path = "incoming-transaction")
public class IncomingTransactionController {


    private IncomingTransactionService incomingTransactionService;


    private AccountService accountService;

    private PaymentRequestDispatcher _paymentRequestDispatcher;

    Logger logger = Logger.getLogger(IncomingTransactionController.class.getName());


    @Autowired
    public IncomingTransactionController(IncomingTransactionService incomingTransactionService, AccountService accountService1, PaymentRequestDispatcher paymentRequestDispatcher) {
        this.incomingTransactionService = incomingTransactionService;
        this.accountService = accountService1;
        _paymentRequestDispatcher = paymentRequestDispatcher;
    }

    // endpoint to receive message when client create a new incoming transaction
    // used by ecommerce platform when customer checkout
    @PostMapping
    public ResponseEntity<?> createIncomingTransaction(@RequestBody NewIncomingTransactionDto newIncomingTransactionDto){


        logger.info("Get new incoming transaction request for account Id =" + newIncomingTransactionDto.accountId().toString());

        // server automatically convert param from string to UUID object
        // no need to check


        IncomingDebitTransaction createdIncomingTransaction =  incomingTransactionService.addIncomingTransaction(
                newIncomingTransactionDto.userId(),
                newIncomingTransactionDto.accountId(),
                newIncomingTransactionDto.destinationAccountId(),
                newIncomingTransactionDto.amount(),
                newIncomingTransactionDto.description(),
                newIncomingTransactionDto.sourceUrl()
        );


        return new ResponseEntity<>(createdIncomingTransaction, HttpStatus.OK);

    }

    // endpoint to receive message when client choose to validate the incoming transaction
    @PutMapping("/validate")
    public ResponseEntity<?> validateIncomingTransaction(@RequestParam("id") UUID transactionId){

        logger.info("Get validate incoming transaction request for transaction Id =" + transactionId.toString());

        IncomingDebitTransaction validatedIncomingTransaction =  incomingTransactionService.validateIncomingTransaction(
                transactionId
        );

        return new ResponseEntity<>(validatedIncomingTransaction, HttpStatus.OK);

    }

    // endpoint to receive message when client choose to refuse the incoming transaction
    @PutMapping("/refuse")
    public ResponseEntity<?> refuseIncomingTransaction(@RequestParam("id") UUID transactionId){


        incomingTransactionService.refusteIncomingTransaction( transactionId);

        return new ResponseEntity<>( HttpStatus.OK);

    }


    // get all incoming transaction for an account
    // used by banking app frontend to retrieve all incoming transaction for an account
    @GetMapping(("/by-account"))
    public ResponseEntity<?> getTransactionByAccountId(@RequestParam("accountId") UUID accountId){

        logger.info("Fetching all incoming transactions for account Id: " + accountId.toString());


        List<IncomingDebitTransaction> transactions =  incomingTransactionService.getTransactionsByAccountId(accountId);

        return new ResponseEntity<>(transactions, HttpStatus.OK);

    }

    // =================== EXCEPTION HANDLERS =================================

    @ExceptionHandler(InsufficientFundException.class)
    public ResponseEntity<String> handleInsufficientFundsException(InsufficientFundException ex) {

        System.out.println("Insufficient Funds Error: " + ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<String> handleItemNotFoundException(ItemNotFoundException ex) {

        System.out.println("Item not found Error: " + ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleGenericRuntimeException(RuntimeException ex) {

        System.out.println("Runtime Error: " + ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }



}
