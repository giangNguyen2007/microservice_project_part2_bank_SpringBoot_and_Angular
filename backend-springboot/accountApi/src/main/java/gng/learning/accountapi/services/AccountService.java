package gng.learning.accountapi.services;


import gng.learning.accountapi.customException.InsufficientFundException;
import gng.learning.accountapi.customException.ItemNotFoundException;
import gng.learning.accountapi.data.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class AccountService {

    private final IAccountRepository _accountRepo;
    private final ITransactionRepository _transactionRepo;

    // Spring will automatically inject the AccountRepository instance here
    public AccountService(IAccountRepository accountRepository, ITransactionRepository transactionRepo) {
        this._accountRepo = accountRepository;
        _transactionRepo = transactionRepo;
    }

    public Account saveAccount(Account account) {

        // JpaRepository's save method handles both creation and update
        Account savedAccount = _accountRepo.save(account);

        return savedAccount;
    }

    public Account createAccount(UUID userId, AccountType accountType) {


        Account account = new Account(userId, accountType);
        // JpaRepository's save method handles both creation and update
        Account createdAccount = _accountRepo.save(account);

        return createdAccount;
    }

    public Account getAccountById(UUID accountId) {
        Account account = _accountRepo.findById(accountId)
                .orElseThrow(() -> new ItemNotFoundException("Account not found"));

        return account;
    }

    public List<Account> getAllAccounts() {
        List<Account> accounts = _accountRepo.findAll();
        return accounts;
    }

    public List<Account> getAccountByUserId(UUID userId) {
        List<Account> myAccounts = _accountRepo.findByUserId(userId)
                .orElseThrow(() -> new ItemNotFoundException("No account found for userId"));
        return myAccounts;
    }


    /**
     * @param accountId
     * @return the deleted accout if successful
     */
    public Account deleteAccount(UUID accountId) {

        Account myAccount = _accountRepo.findById(accountId)
                .orElseThrow( () -> new ItemNotFoundException("Account not found")) ;

        _accountRepo.delete(myAccount);

        return myAccount;
    }

    @Transactional
    public Account deposit(UUID accountId, Integer amount) {

        if ( amount <= 0 ) {
            throw new IllegalArgumentException("Amount must be a positive integer");
        }

        // method with locking control
        Account myAccount = _accountRepo.findForUpdateWithLock(accountId)
                .orElseThrow( () -> new ItemNotFoundException(" Account not found")) ;


        myAccount.setBalance( myAccount.getBalance() + amount );


        return myAccount;
    }

    // called for internal transfer between two accounts inside the system
    // executed immediately without need for reserve
    @Transactional
    public Account executeInternalTransfer(UUID accountId, UUID destinationAccountId, Integer amount) {

        if ( amount <= 0 ) {
            throw new IllegalArgumentException("Amount must be a positive integer");
        }

        // method with locking control
        Account sourceAccount = _accountRepo.findForUpdateWithLock(accountId)
                .orElseThrow( () -> new ItemNotFoundException("Source Account not found")) ;

        Account destinationAccount = _accountRepo.findForUpdateWithLock(destinationAccountId)
                .orElseThrow( () -> new ItemNotFoundException("Destination Account not found")) ;

        // check sufficient fund
        if ( sourceAccount.getBalance() - amount < 0) {
            throw new InsufficientFundException("Insufficient balance for the transaction");
        }

        sourceAccount.setBalance( sourceAccount.getBalance() - amount );
        destinationAccount.setBalance( destinationAccount.getBalance() + amount );

        // no need to explicitly save, as the transaction will commit the changes

        return sourceAccount;
    }

    // called when a debit transaction is created
    // the amount is reserved until the transaction is confirmed or cancelled
    // amount should be positive integer
    @Transactional
    public Account reserveBalance(UUID accountId, Integer amount) {

        if ( amount <= 0 ) {
            throw new IllegalArgumentException("Amount must be a positive integer");
        }

        // method with locking control
        Account myAccount = _accountRepo.findForUpdateWithLock(accountId)
                .orElseThrow( () -> new ItemNotFoundException("Account not found")) ;

        // check sufficient fund
        if ( myAccount.getBalance() - amount < 0) {
            throw new InsufficientFundException("Insufficient balance for the transaction");
        }

        myAccount.setBalance( myAccount.getBalance() - amount );
        myAccount.setReservedBalance( myAccount.getReservedBalance() + amount );

        // no need to explicitly save, as the transaction will commit the changes
        _accountRepo.save(myAccount);

        return myAccount;
    }

    // called when a transaction is confirmed
    // the reserved amount is deducted from the reserved balance
    @Transactional
    public Account consumeReservedBalance(UUID accountId, Integer amount) {

        if ( amount <= 0 ) {
            throw new IllegalArgumentException("Amount must be a positive integer");
        }

        Account myAccount = _accountRepo.findForUpdateWithLock(accountId)
                .orElseThrow( () -> new ItemNotFoundException("Account not found")) ;

        // check sufficient fund
        if ( myAccount.getReservedBalance() - amount < 0) {
            throw new InsufficientFundException("Insufficient  reserved balance for the transaction");
        }

        myAccount.setReservedBalance( myAccount.getReservedBalance() - amount );

        // no need to explicitly save, as the transaction will commit the changes
        _accountRepo.save(myAccount);

        return myAccount;
    }

    // called when a transaction is cancelled
    // the reserved amount is returned to the available balance
    @Transactional
    public Account releaseBalance(UUID accountId, Integer amount) {

        if ( amount <= 0 ) {
            throw new IllegalArgumentException("Amount must be a positive integer");
        }

        Account myAccount = _accountRepo.findForUpdateWithLock(accountId)
                .orElseThrow( () -> new ItemNotFoundException("Account not found")) ;

        // check sufficient fund
        if ( myAccount.getReservedBalance() - amount < 0) {
            throw new InsufficientFundException("Insufficient  reserved balance for the transaction");
        }
        myAccount.setBalance( myAccount.getBalance() + amount );
        myAccount.setReservedBalance( myAccount.getReservedBalance() - amount );

        // no need to explicitly save, as the transaction will commit the changes
        _accountRepo.save(myAccount);

        return myAccount;
    }
}
