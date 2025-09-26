package gng.learning.accountapi.controllers;

import gng.learning.accountapi.customException.InsufficientFundException;
import gng.learning.accountapi.customException.ItemNotFoundException;
import gng.learning.accountapi.data.Account;
import gng.learning.accountapi.data.AccountType;
import gng.learning.accountapi.services.AccountService;
import gng.learning.accountapi.services.grpc.UserInfoGrpcClientService;
import gng.learning.accountapi.services.rabbitmq.PaymentRequestDispatcher;
import gng.learning.grpc.UserInfoResponse;
import gng.learning.sharedLibrary.dtos.accountController.CreateAccountDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

@RestController
@RequestMapping(path = "account")
public class AccountController {


    private final AccountService _accountService;
    private final PaymentRequestDispatcher _paymentRequestDispatcher;

    private final UserInfoGrpcClientService _userInfoGrpcClientService;

    Logger logger = Logger.getLogger(AccountController.class.getName());


    @Autowired
    public AccountController(AccountService accountService, PaymentRequestDispatcher paymentRequestDispatcher, UserInfoGrpcClientService userInfoGrpcClientService) {
        _accountService = accountService;
        _paymentRequestDispatcher = paymentRequestDispatcher;
        _userInfoGrpcClientService = userInfoGrpcClientService;
    }

    @GetMapping()
    public ResponseEntity<?> getAccountByUserId(HttpServletRequest request){

        String userId = request.getHeader("X-User-Id");

        logger.info("Fetching account for userId: " + userId);

        List<Account> myAccounts =  _accountService.getAccountByUserId(  UUID.fromString(userId) );

        return new ResponseEntity<>(myAccounts, HttpStatus.OK);

    }

    @GetMapping(path = "/all")
    public ResponseEntity<?> getAllAccounts(){

         List<Account> myAccounts =  _accountService.getAllAccounts();

        return new ResponseEntity<>(myAccounts, HttpStatus.OK);

    }

    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody CreateAccountDto createAccountDto, HttpServletRequest request){

         String userId = request.getHeader("X-User-Id");

        // contact User API to verify valid UserID
        UserInfoResponse userInfoResponse =  _userInfoGrpcClientService.getUserInfo( UUID.fromString(userId) );

        if (!userInfoResponse.getFound()) {
            return new ResponseEntity<>("Invalid User ID", HttpStatus.BAD_REQUEST);
        }

        // check account type in DTO
        if ( createAccountDto.accountType() == null ) {
            return new ResponseEntity<>("Account type is required", HttpStatus.BAD_REQUEST);
        }

        AccountType accountType;
        try {
            accountType = AccountType.valueOf( createAccountDto.accountType().toUpperCase() );
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>("Invalid account type", HttpStatus.BAD_REQUEST);
        }


        logger.info("Verification success. Create account for userName = : " + userInfoResponse.getUserName() );

        Account createdAccount =  _accountService.createAccount( UUID.fromString(userId), accountType );

        return new ResponseEntity<>(createdAccount, HttpStatus.OK);

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
