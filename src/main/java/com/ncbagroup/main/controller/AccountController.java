package com.ncbagroup.main.controller;

import com.ncbagroup.main.model.request.Account;
import com.ncbagroup.main.service.AccountServiceInterface;
import com.ncbagroup.main.util.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Validated
public class AccountController {
    private AccountServiceInterface accountServiceInterface;
    private Utils utils;

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    public AccountController(AccountServiceInterface accountServiceInterface, Utils utils) {
        this.accountServiceInterface = accountServiceInterface;
        this.utils = utils;
    }

    // Create a new account
    @PostMapping(path = "create-account", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAccount(@RequestBody Account account) throws Exception {

        if (!account.isValid()) {
            // Handle validation errors
            return ResponseEntity.badRequest().body( utils.convertMapToJson(utils.buildValidationErrorResponse(account)));
        }

        JSONObject incomingRequestJSON = new JSONObject(account);
        logger.info("-------------------------------------------------------------------");
        logger.info("Incoming request: \n{}", incomingRequestJSON);
        logger.info("-------------------------------------------------------------------");

        ResponseEntity<?> responseEntity = accountServiceInterface.CreateAccount(account);

        utils.logResponseEntity(responseEntity, "api response", false);

        return responseEntity;
    }

    // Get All Accounts
    @GetMapping(path = "accounts", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAccounts() {

        return accountServiceInterface.GetAllAccounts();
    }


    // Get an account by ID
//    @GetMapping("/{accountId}")
//    public ResponseEntity<Account> getAccount(@PathVariable String accountId) {
//        Optional<Account> account = accountRepository.findById(accountId);
//        return account.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
//                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
//    }
//
//    // Update an account by ID
//    @PutMapping("/{accountId}")
//    public ResponseEntity<Account> updateAccount(@PathVariable String accountId, @RequestBody Account updatedAccount) {
//        Optional<Account> existingAccount = accountRepository.findById(accountId);
//        if (existingAccount.isPresent()) {
//            Account account = existingAccount.get();
//            account.setIban(updatedAccount.getIban());
//            account.setBicSwift(updatedAccount.getBicSwift());
//            accountRepository.save(account);
//            return new ResponseEntity<>(account, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//
//    // Delete an account by ID
//    @DeleteMapping("/{accountId}")
//    public ResponseEntity<Void> deleteAccount(@PathVariable String accountId) {
//        if (accountRepository.existsById(accountId)) {
//            accountRepository.deleteById(accountId);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
}
