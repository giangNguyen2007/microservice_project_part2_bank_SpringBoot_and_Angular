package gng.learning.accountapi.services;


import gng.learning.accountapi.customException.ItemNotFoundException;
import gng.learning.accountapi.data.*;
import gng.learning.accountapi.services.rabbitmq.PaymentRequestDispatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@Service
public class TransactionService {

    private final AccountService _accountService;
    private final ITransactionRepository _transactionRepo;

    private final PaymentRequestDispatcher _paymentRequestDispatcher;

    private HttpClientService httpClientService;


    Logger logger = Logger.getLogger(TransactionService.class.getName());

    // Spring will automatically inject the AccountRepository instance here
    public TransactionService(IAccountRepository accountRepository, AccountService accountService, ITransactionRepository transactionRepo, PaymentRequestDispatcher paymentRequestDispatcher, HttpClientService httpClientService) {
        _accountService = accountService;
        _transactionRepo = transactionRepo;
        _paymentRequestDispatcher = paymentRequestDispatcher;
        this.httpClientService = httpClientService;
    }

    @Transactional
    public Transaction addDepositTransaction(UUID userId, UUID accountId, Integer amount, String description) {

        // checks are done inside  AccountService methods
        Account myAccount = _accountService.deposit( accountId, amount);

        if (myAccount.getUserId().equals(userId) == false) {
            throw new IllegalArgumentException("User ID does not match account owner");
        }


        // if transactions are executed successfully, => register  transactions

        // registers debit transactions in the database
        Transaction newDepositTransaction = new Transaction(
                amount, description, TransactionStatus.COMPLETED
        );
        newDepositTransaction.setAccount(myAccount);
        newDepositTransaction.setAccountId(myAccount.getId());
        newDepositTransaction.setDestinationAccountId(null);
        newDepositTransaction.setIsDebit(false);
        Transaction ts = _transactionRepo.save(newDepositTransaction); // Save the updated account


        logger.info("Finished executing internal transfer transaction with ID: " + newDepositTransaction.getId() + " for account ID: " + myAccount.getId());

        return ts;

    }
    
    
    // Internal transaction is executed within the system, no need to dispatch payment request
    // transaction is executed immediately
    @Transactional
    public Transaction addInternalTransferTransaction(UUID userId, UUID debitAccountId, UUID creditAccountId, Integer amount, String description) {

        // checks are done inside  AccountService methods
        Account debitAccount = _accountService.executeInternalTransfer( debitAccountId, creditAccountId, amount);

        if (debitAccount.getUserId().equals(userId) == false) {
            throw new IllegalArgumentException("User ID does not match account owner");
        }

        logger.info("Created new transaction with amount: " + amount + " and description: " + description + " for account ID: " + debitAccount.getId() + ". Dispatching payment request...");

        // if transactions are executed successfully, => register debit + credit transactions

        // registers debit transactions in the database
        Transaction newDebitTransaction = new Transaction(
                amount, description, TransactionStatus.COMPLETED
        );
        newDebitTransaction.setAccount(debitAccount);
        newDebitTransaction.setAccountId(debitAccount.getId());
        newDebitTransaction.setDestinationAccountId(creditAccountId);
        newDebitTransaction.setIsDebit(true);
        Transaction ts = _transactionRepo.save(newDebitTransaction); // Save the updated account


        // register transaction for destination account
        Account creditAccount = _accountService.getAccountById(creditAccountId);
        Transaction newCreditTransaction = new Transaction(
                amount, description, TransactionStatus.COMPLETED
        );
        newCreditTransaction.setAccount(creditAccount);
        newCreditTransaction.setAccountId(creditAccountId);
        newCreditTransaction.setDestinationAccountId(debitAccount.getId());
        newCreditTransaction.setIsDebit(false);
        _transactionRepo.save(newCreditTransaction);


        logger.info("Finished executing internal transfer transaction with ID: " + newDebitTransaction.getId() + " for account ID: " + debitAccount.getId());

        return ts;

    }



    // add new transaction in pending state
    // account balance not changed yet until transaction confirmation
    @Transactional
    public Transaction addExternalDebitTransaction(UUID userId, UUID accountId, UUID destinationId, Integer amount, String description, String sourceUrl) {

        // checks are done inside  AccountService methods
        Account myAccount = _accountService.reserveBalance( accountId, amount);

        if (myAccount.getUserId().equals(userId) == false) {
            throw new IllegalArgumentException("User ID does not match account owner");
        }

        // if reservation suceeds, create pending transaction
        Transaction newTransaction = new Transaction(
                amount, description, TransactionStatus.PENDING
        );

        logger.info("Created new transaction with amount: " + amount + " and description: " + description + " for account ID: " + myAccount.getId() + ". Dispatching payment request...");

        // save new transaction
        newTransaction.setAccount(myAccount);
        newTransaction.setAccountId(myAccount.getId());
        newTransaction.setDestinationAccountId(destinationId);
        newTransaction.setIsDebit(true);
        newTransaction.setSourceUrl(sourceUrl);
        Transaction savedTransaction = _transactionRepo.save(newTransaction); // Save the updated account



        logger.info("Created pending transaction with ID: " + newTransaction.getId() + " for account ID: " + myAccount.getId());


        if (savedTransaction == null) {
            throw new RuntimeException("Failed to save the new transaction");
        }

        _paymentRequestDispatcher.dispatchPaymentRequest(savedTransaction);

        return savedTransaction;

    }

    /**
     * @param transactionId
     * @return change transaction status from PENDING to CONFIRMED update Account balance
     */
    @Transactional
    public Transaction confirmTransactionSuccess(UUID transactionId) {

        logger.info("Receive confirmation success transaction with ID: " + transactionId);

        Transaction myTransaction = _transactionRepo.findById(transactionId)
                .orElseThrow(() -> new ItemNotFoundException("Transaction not found"));

        Account myAccount = _accountService.getAccountById(myTransaction.getAccountId());

        if ( myAccount == null) {
            throw new ItemNotFoundException("Account not found for the transaction");
        }



        // update balance and transaction status
        myTransaction.setStatus(TransactionStatus.COMPLETED);
        _accountService.consumeReservedBalance( myAccount.getId(), myTransaction.getAmount() );

        logger.info("Transaction with ID: " + myTransaction.getId() + " for account ID: " + myAccount.getId() + " is CONFIRMED. Updated account balance: " + myAccount.getBalance());

        // save transaction with updated status
        _transactionRepo.save(myTransaction);

        // save account with updated balance
        _accountService.saveAccount(myAccount);

        // notify external system of the successful transaction
        if ( myTransaction.getSourceUrl() != null ){

            httpClientService.sendPaymentResult(myTransaction.getSourceUrl(), Boolean.TRUE);
        }


        return myTransaction;

    }

    public Transaction confirmTransactionFailure( UUID transactionId) {

        logger.info("Receive confirmation failure transaction with ID: " + transactionId);

        Transaction myTransaction = _transactionRepo.findById(transactionId)
                .orElseThrow(() -> new ItemNotFoundException("Transaction not found"));

        // update balance and transaction status
        myTransaction.setStatus(TransactionStatus.FAILED);

        _transactionRepo.save(myTransaction);

        logger.info("Transaction with ID: " + myTransaction.getId() + " is marked as FAILED. Account balance remains: ");


        return myTransaction;

    }

    public List<Transaction> getTransactionsByAccountId(UUID accountId) {

         List<Transaction> transactions = _transactionRepo.findByAccountId(accountId);

//        if (transactions.isEmpty()) {
//
//            throw new ItemNotFoundException("no transaction found for account Id");
//        }

        return transactions;
    }


}
