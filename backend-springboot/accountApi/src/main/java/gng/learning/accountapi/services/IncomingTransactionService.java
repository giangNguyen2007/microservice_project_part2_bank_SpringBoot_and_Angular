package gng.learning.accountapi.services;


import gng.learning.accountapi.customException.ItemNotFoundException;
import gng.learning.accountapi.data.*;
import gng.learning.accountapi.services.rabbitmq.PaymentRequestDispatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class IncomingTransactionService {

    private final AccountService _accountService;
    private final TransactionService _transactionService;

    private HttpClientService httpClientService;

    private final IComingTransactionRepository _incomingTransactionRepo;

    private final PaymentRequestDispatcher _paymentRequestDispatcher;

    Logger logger = Logger.getLogger(IncomingTransactionService.class.getName());

    // Spring will automatically inject the AccountRepository instance here
    public IncomingTransactionService(AccountService accountService, TransactionService transactionService, IComingTransactionRepository incomingTransactionRepo, PaymentRequestDispatcher paymentRequestDispatcher) {
        _accountService = accountService;
        _transactionService = transactionService;
        _incomingTransactionRepo = incomingTransactionRepo;
        _paymentRequestDispatcher = paymentRequestDispatcher;
    }



    // add new transaction in pending state
    // account balance not changed yet until transaction confirmation
    public IncomingDebitTransaction addIncomingTransaction(UUID userId, UUID accountId, UUID destinationAccountId, Integer amount, String description, String sourceUrl) {

        if(amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be null or empty");
        }

        logger.info( "source url = " + sourceUrl );

        if( sourceUrl == null || sourceUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Source URL cannot be null or empty");
        }

        Account myAccount = _accountService.getAccountById(accountId);

        if ( !userId.equals( myAccount.getUserId() ) ) {
            throw new IllegalArgumentException("User ID does not match account owner");
        }


        IncomingDebitTransaction newIncomingTransaction = new IncomingDebitTransaction(
                amount, description
        );


        newIncomingTransaction.setAccountId(accountId);
        newIncomingTransaction.setSourceUrl(sourceUrl);
        newIncomingTransaction.setDestinationAccountId(destinationAccountId);


        // save new transaction

        IncomingDebitTransaction savedTransaction = _incomingTransactionRepo.save(newIncomingTransaction); // Save the updated account

        logger.info("Created new transaction with amount: " + amount + " and description: " + description + " for account ID: " + myAccount.getId() + ". Awaiting validation...");

        if (savedTransaction == null) {
            throw new RuntimeException("Failed to save the new transaction");
        }

        return savedTransaction;


    }

    /**
     * @param id
     * @return change incoming transaction status from invalidated to validated, then add new Transaction to the account
     */
    public IncomingDebitTransaction validateIncomingTransaction(UUID id) {

        logger.info("Validating incoming transaction with ID: " + id);

        IncomingDebitTransaction incomingTransaction = _incomingTransactionRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Transaction not found"));

        Account myAccount = _accountService.getAccountById(incomingTransaction.getAccountId());

        if ( myAccount == null) {
            throw new ItemNotFoundException("Account not found for the transaction");
        }


        if (!incomingTransaction.getValidated()) {
            incomingTransaction.setValidated(true);

            // add new transaction to the account
            _transactionService.addExternalDebitTransaction(
                    myAccount.getUserId(),
                    myAccount.getId(),
                    incomingTransaction.getDestinationAccountId(),
                    incomingTransaction.getAmount(),
                    incomingTransaction.getDescription(),
                    incomingTransaction.getSourceUrl()
            );

        }


        _incomingTransactionRepo.save(incomingTransaction);

        return incomingTransaction;

    }

    public void refusteIncomingTransaction(UUID id) {

        logger.info("Refuse incoming transaction with ID: " + id);

        IncomingDebitTransaction incomingTransaction = _incomingTransactionRepo.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Transaction not found"));


        // notify the ecommerce app that the transaction is refused
        httpClientService.sendPaymentResult(incomingTransaction.getSourceUrl(), Boolean.FALSE);


    }


    public List<IncomingDebitTransaction> getTransactionsByAccountId(UUID accountId) {

         List<IncomingDebitTransaction> transactions = _incomingTransactionRepo.findByAccountId(accountId);

//        if (transactions.isEmpty()) {
//
//            throw new ItemNotFoundException("no transaction found for account Id");
//        }

        return transactions;
    }


}
