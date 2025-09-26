package gng.learning.accountapi.controllers;

import gng.learning.accountapi.customException.InsufficientFundException;
import gng.learning.accountapi.customException.ItemNotFoundException;
import gng.learning.accountapi.data.Transaction;
import gng.learning.accountapi.services.AccountService;
import gng.learning.accountapi.services.TransactionService;
import gng.learning.accountapi.services.rabbitmq.PaymentRequestDispatcher;
import gng.learning.sharedLibrary.dtos.accountController.NewTransactionDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;


record CreateDepositDto(
        @NotNull
        UUID accountId ,
        @NotBlank @Min(value = 1, message = "Amount must be greater than zero")
        Integer amount,
        @NotBlank @Max(value = 255, message = "Description must be at most 255 characters")
        String description

) {}



@RestController
@RequestMapping(path = "transaction")
public class TransactionController {


    private TransactionService transactionService;

    private AccountService accountService;

    private PaymentRequestDispatcher _paymentRequestDispatcher;

    Logger logger = Logger.getLogger(TransactionController.class.getName());


    @Autowired
    public TransactionController(TransactionService transactionService, AccountService accountService1, PaymentRequestDispatcher paymentRequestDispatcher) {
        this.transactionService = transactionService;
        this.accountService = accountService1;
        _paymentRequestDispatcher = paymentRequestDispatcher;
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> createDepositTransaction(@RequestBody CreateDepositDto createDepositDto, @RequestHeader("X-User-Id") String userIdHeader){

        UUID userId = UUID.fromString(userIdHeader);

        logger.info("Get new transaction request for account Id =" + createDepositDto.accountId().toString());

        Transaction ts = transactionService.addDepositTransaction(userId, createDepositDto.accountId(), createDepositDto.amount(), createDepositDto.description());

        URI location = ServletUriComponentsBuilder
                .fromPath("/transaction/single-transaction")
                .path("/{id}")
                .buildAndExpand(ts.getId())
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<>( headers, HttpStatus.CREATED);

    }


    @PostMapping("/external")
    public ResponseEntity<?> createExternalDebitTransaction(@Valid @RequestBody NewTransactionDto newTransactionDto, @RequestHeader("X-User-Id") String userIdHeader){

        UUID userId = UUID.fromString(userIdHeader);

        if (!newTransactionDto.isExternalTransaction()){
            throw new IllegalArgumentException("This endpoint is for external transfer only. For internal transfer, use /internal-transfer endpoint");
        }

        logger.info("Get new transaction request for account Id =" + newTransactionDto.accountId().toString());


        Transaction createdTransaction =  transactionService.addExternalDebitTransaction(userId, newTransactionDto.accountId(), newTransactionDto.destinationAccountId(), newTransactionDto.amount(), newTransactionDto.description(), null);

        URI location = ServletUriComponentsBuilder
                .fromPath("/transaction/single-transaction")
                .path("/{id}")
                .buildAndExpand(createdTransaction.getId())
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<>( headers, HttpStatus.CREATED);

    }

    @PostMapping("/internal-transfer")
    public ResponseEntity<?> createInternalTransferTransaction(@Valid @RequestBody NewTransactionDto newTransactionDto, @RequestHeader("X-User-Id") String userIdHeader){

        UUID userId = UUID.fromString(userIdHeader);

        if (newTransactionDto.isExternalTransaction()){
            throw new IllegalArgumentException("This endpoint is for internal transfer only. For external transaction, use /external endpoint");
        }

        logger.info("Get new transaction request for account Id =" + newTransactionDto.accountId().toString());


        logger.info("destination account Id =" + newTransactionDto.destinationAccountId().toString());

        Transaction debitTransaction = transactionService.addInternalTransferTransaction(userId, newTransactionDto.accountId(), newTransactionDto.destinationAccountId(), newTransactionDto.amount(), newTransactionDto.description());

        URI location = ServletUriComponentsBuilder
                .fromPath("/transaction/single-transaction")
                .path("/{id}")
                .buildAndExpand(debitTransaction.getId())
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(location);

        return new ResponseEntity<>( headers, HttpStatus.CREATED);

    }


    @GetMapping("/by-account")
    public ResponseEntity<?> getTransactionByAccountId(@RequestParam("accountId") UUID accountId){

        logger.info("Fetching all transactions for account Id: " + accountId.toString());

        // server automatically convert param from string to UUID object
        // no need to check

        System.out.println("find transaction for account Id =" + accountId.toString());

        List<Transaction> transactions =  transactionService.getTransactionsByAccountId(accountId);

        return new ResponseEntity<>(transactions, HttpStatus.OK);

    }

    @GetMapping("/single-transaction")
    public ResponseEntity<?> getSingleTransactionById(@RequestParam("accountId") UUID accountId){

        logger.info("Fetching all transactions for account Id: " + accountId.toString());

        // server automatically convert param from string to UUID object
        // no need to check

        System.out.println("find transaction for account Id =" + accountId.toString());

        List<Transaction> transactions =  transactionService.getTransactionsByAccountId(accountId);

        return new ResponseEntity<>(transactions, HttpStatus.OK);

    }

    // =================== EXCEPTION HANDLERS =================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalException(IllegalArgumentException ex) {

        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


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
