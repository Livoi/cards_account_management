package com.ncbagroup.main.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ncbagroup.main.entity.AccountEntity;
import com.ncbagroup.main.model.request.Account;
import com.ncbagroup.main.repository.AccountRepository;
import com.ncbagroup.main.service.AccountServiceInterface;
import com.ncbagroup.main.util.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
@Validated
public class AccountController {
    private AccountServiceInterface accountServiceInterface;
    private Utils utils;

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final AccountRepository accountRepository;

    public AccountController(AccountServiceInterface accountServiceInterface, Utils utils,
                             AccountRepository accountRepository) {
        this.accountServiceInterface = accountServiceInterface;
        this.utils = utils;
        this.accountRepository = accountRepository;
    }

    // Create a new account
    @PostMapping(path = "create-account", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAccount(@RequestBody Account account) throws Exception {

        if (!account.isValid()) {
            // Handle validation errors
            return ResponseEntity.badRequest().body( utils.convertMapToJson(utils.buildValidationErrorResponse(account)));
        }



        ResponseEntity<?> responseEntity = accountServiceInterface.CreateAccount(account);

        utils.logResponseEntity(responseEntity, "api response", false);

        return responseEntity;
    }
    // Get an account by ID
    @GetMapping("/accounts/{accountId}")
    public ResponseEntity<?> getAccount(@PathVariable Long accountId) {

        try {
            Account account = accountServiceInterface.GetAccount(accountId);

            if(account == null){
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("id", accountId);
                responseBody.put("status", "Not Found");
                ResponseEntity<?> responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
                utils.logResponseEntity(responseEntity, "No Account found with id: "+ accountId , false);
                return responseEntity;
            }

            ResponseEntity responseEntity = ResponseEntity.ok().body(account);

            utils.logResponseEntity(responseEntity, "Account found with id: "+ accountId , true);

            return responseEntity;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
//
//    // Update an account by ID
//    @PutMapping("/{accountId}")
//    public ResponseEntity<Account> updateAccount(@PathVariable Long accountId, @RequestBody Account updatedAccount) {
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
