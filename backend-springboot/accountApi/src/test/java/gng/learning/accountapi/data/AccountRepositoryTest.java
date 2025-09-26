package gng.learning.accountapi.data;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AccountRepositoryTest {

    @Autowired
    private IAccountRepository _accountRepo;

    @Autowired
    private ITransactionRepository _transactionRepo;

    private final UUID _myUserId = UUID.randomUUID();
    private UUID _myTransactionId;
    @BeforeEach
    void setUp() {

        Account newAccount = new Account(
                _myUserId,
                AccountType.COMPTE_COURANT
        );

        Account savedAccount = _accountRepo.save(newAccount);

        Transaction newTransaction = new Transaction(100, "my first transfer", TransactionStatus.PENDING);
        //newTransaction.setId(_myTransactionId);

        newTransaction.setAccount(savedAccount);
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
    @Transactional
    void findByUserId() {

        List<Account> myAccount = _accountRepo.findByUserId(_myUserId)
                .orElseThrow(() -> new RuntimeException("account not found"));

//        Set<Transaction> myTransactions = myAccount.getTransactions();
//
//        assertNotNull(myAccount);
//        assertEquals(_myUserId.toString(), myAccount.getUserId().toString());

//        assertEquals(1, myTransactions.size());
    }

    @Test
    void getAllAccount() {

        List<Account> accountList =  _accountRepo.findAll();
        assertEquals(1, accountList.size() );
    }

    @Test
    @Transactional
    void addTransaction() {

        Transaction newTransaction = new Transaction(100, "my transfer", TransactionStatus.PENDING);

        List<Account>  myAccount = _accountRepo.findByUserId(_myUserId)
                .orElseThrow(() -> new RuntimeException("account not found"));;

        // access the Set<Transaction>
//        myAccount.addTransaction(newTransaction);
//
//        _accountRepo.save(myAccount);

        List<Transaction> allTransactions =  _transactionRepo.findAll();
        assertEquals(2, allTransactions.size() );
    }

    @Test
    void findTransactionById() {

        Transaction myTransaction = _transactionRepo.findById(_myTransactionId).orElseThrow();

        assertNotNull(myTransaction);
    }
}