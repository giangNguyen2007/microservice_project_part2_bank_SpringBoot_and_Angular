package gng.learning.accountapi.services;

import gng.learning.accountapi.customException.InsufficientFundException;
import gng.learning.accountapi.data.*;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        properties = {
            "spring.datasource.url=jdbc:h2:mem:testdb",
            "spring.datasource.driver-class-name=org.h2.Driver",
            "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
            "spring.jpa.hibernate.ddl-auto=create-drop"
        }
)
class TransactionServiceTest {

    @Autowired
    private IAccountRepository _accountRepo;

    @Autowired
    private ITransactionRepository _transactionRepo;

    @Autowired
    private TransactionService _transactionService;

    private final UUID _myUserId = UUID.randomUUID();

    private UUID _myAccountId;
    private UUID _myTransactionId;
    @BeforeEach
    void setUp() {

        // seed database with one account and one transaction
        Account newAccount = new Account(
                _myUserId,
                AccountType.COMPTE_COURANT
        );

        Account savedAccount = _accountRepo.save(newAccount);

        _myAccountId = savedAccount.getId();

        Transaction newTransaction = new Transaction(100, "my first transfer", TransactionStatus.PENDING);
        //newTransaction.setId(_myTransactionId);

        newTransaction.setAccount(savedAccount);
        newTransaction.setAccountId(savedAccount.getId());

        savedAccount.addTransaction(newTransaction);
        Transaction savedTransaction = _transactionRepo.save(newTransaction);

        _myTransactionId = savedTransaction.getId();

    }

    @AfterEach
    void tearDown() {

        _accountRepo.deleteAll();
        _transactionRepo.deleteAll();
    }



    @Test
    void addNewTransaction_ValidAmount() {

        Transaction newTransaction = assertDoesNotThrow(() -> {
            return _transactionService.addExternalDebitTransaction(_myUserId ,_myAccountId, UUID.randomUUID() ,50, "Transfer from friend", null);
        });

        assertEquals(50, newTransaction.getAmount());
        assertEquals(TransactionStatus.PENDING, newTransaction.getStatus());

    }

    @Test
    @Transactional
    void confirmTransaction_validId() {

        Transaction newTransaction = assertDoesNotThrow(() -> {
            return _transactionService.confirmTransactionSuccess(_myTransactionId);
        });

        // get the account

        Account myAccount = _accountRepo.findById(_myAccountId).orElseThrow();

        Transaction transaction = _transactionRepo.findById(_myTransactionId).orElseThrow();

        assertEquals(100, myAccount.getBalance());
        assertEquals(TransactionStatus.COMPLETED, transaction.getStatus());

    }

    @Test
    @Transactional
    void confirmTransaction_invalidAmount() {

        // confirm +100 transaction => balance = 100
        Transaction firstTransaction = assertDoesNotThrow(() -> {
            return _transactionService.confirmTransactionSuccess(_myTransactionId);
        });

        // add -120 transaction
        InsufficientFundException ex = assertThrows(InsufficientFundException.class, () -> {
             _transactionService.addExternalDebitTransaction(_myUserId ,_myAccountId, UUID.randomUUID(),120, "Transfer from friend", null);
        });

    }

    @Test
    @Transactional
    void confirmTransaction_invalidAmount_atConfirmation() {

        // confirm +100 transaction => balance = 100
        Transaction firstTransaction = assertDoesNotThrow(() -> {
            return _transactionService.confirmTransactionSuccess( _myTransactionId);
        });

        // add -120 transaction
        InsufficientFundException ex = assertThrows(InsufficientFundException.class, () -> {
            _transactionService.addExternalDebitTransaction(_myUserId ,_myAccountId, UUID.randomUUID(),120, "Transfer from friend", null);
        });

    }

    @Test
    void findTransactionByAccountId() {

        List<Transaction> newTransaction = assertDoesNotThrow(() -> {
            return _transactionService.getTransactionsByAccountId(_myAccountId);
        });

        System.out.println("gng transaction size =" + newTransaction.size());

        assertEquals(1, newTransaction.size());

    }
}