package gng.learning.accountapi.services;

import gng.learning.accountapi.customException.InsufficientFundException;
import gng.learning.accountapi.customException.ItemNotFoundException;
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
class AccountServiceTest {

    @Autowired
    private IAccountRepository _accountRepo;

    @Autowired
    private ITransactionRepository _transactionRepo;

    @Autowired
    private AccountService _accountService;

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
    void getAllAccounts() {

        List<Account> accountList = _accountService.getAllAccounts();
        assertEquals(1, accountList.size());
    }


    @Test
    void createAccount() {
        Account newAccount = _accountService.createAccount( UUID.randomUUID(), AccountType.COMPTE_COURANT);
        List<Account> accountList = _accountService.getAllAccounts();
        assertEquals(2, accountList.size());
    }

    @Test
    void getAccountById_ValidId() {
        Account myAccount = _accountService.getAccountById(_myAccountId);
        assertNotNull(myAccount);
    }

    @Test
    void getAccountById_InValidId() {

        ItemNotFoundException ex = assertThrows( ItemNotFoundException.class, () -> {
            _accountService.getAccountById(UUID.randomUUID());
        });

        assertEquals("Account not found", ex.getMessage());
    }



    @Test
    void getAccountByUserId_ValidUserId() {

        List<Account> account = assertDoesNotThrow(() -> {
            return _accountService.getAccountByUserId(_myUserId);
        });

//        assertEquals(_myUserId, account.getUserId());

    }

    @Test
    void getAccountByUserId_InvalidUserId() {

        ItemNotFoundException ex = assertThrows( ItemNotFoundException.class, () -> {
            _accountService.getAccountByUserId(UUID.randomUUID());
        });

        assertEquals("No account found for userId", ex.getMessage());
    }


}