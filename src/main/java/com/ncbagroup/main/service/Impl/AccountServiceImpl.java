package com.ncbagroup.main.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ncbagroup.main.entity.AccountEntity;
import com.ncbagroup.main.model.request.Account;
import com.ncbagroup.main.model.request.Card;
import com.ncbagroup.main.repository.AccountRepository;
import com.ncbagroup.main.service.AccountServiceInterface;
import com.ncbagroup.main.service.CardServiceInterface;
import com.ncbagroup.main.util.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class AccountServiceImpl implements AccountServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(AccountServiceImpl.class);

    private Utils utils;
    private AccountRepository accountRepository;
    private CardServiceInterface cardServiceInterface;

    public AccountServiceImpl(Utils utils, AccountRepository accountRepository, CardServiceInterface cardServiceInterface) {
        this.utils = utils;
        this.accountRepository = accountRepository;
        this.cardServiceInterface = cardServiceInterface;
    }
    @Override
    public ResponseEntity<String> CreateAccountWithCards(Account account, List<Card> cardList) {
        try {
            // Save the account
            if(CreateAccount(account).getStatusCode() == HttpStatus.OK){
                String savedAccount = (String) CreateAccount(account).getBody();
                Account accountResp = (Account) utils.setJsonStringToObject(savedAccount,
                        Account.class);

                // Associate cards with the account
                for (Card card : cardList) {
                    card.setAccountId(accountResp.getId());
                    cardServiceInterface.CreateNewCard(card);
                }

                return new ResponseEntity<>("Account with cards created successfully", HttpStatus.CREATED);
            }

            // Handle exceptions or log errors
            return new ResponseEntity<>("Failed to create account with cards", HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            // Handle exceptions or log errors
            return new ResponseEntity<>("Failed to create account with cards", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<?> CreateAccount(Account account) {
        AccountEntity savedAccount = null;
        try {
            Account accountResp = GetAccountByClientId(account.getClientId());
            if(accountResp == null){

                AccountEntity accountEntity = utils.convertDtoToEntity(account, AccountEntity.class);

                savedAccount = accountRepository.save(accountEntity);

                Account savedAccountResp = utils.convertEntityToDto(savedAccount, Account.class);

                ResponseEntity<?> responseEntity = ResponseEntity.ok().body(savedAccountResp);

                utils.logResponseEntity(responseEntity, "create account", false);

                return responseEntity;
            }

            return ResponseEntity.ok().body(accountResp);
        } catch (Exception e) {

            ResponseEntity<?> responseEntity = ResponseEntity.internalServerError().body(e.getMessage());
            try {
                utils.logResponseEntity(responseEntity, "create account", true);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
            return responseEntity;
        }

    }

    @Override
    public Account GetAccount(Long accountId) {
        try {
            AccountEntity accountEntity = accountRepository.findTopById(accountId);

            if (accountEntity != null) {
                JSONObject accountEntityJSON = new JSONObject(accountEntity);

                logger.info("-------------------------------------------------------------------");
                logger.info("Account exists with id : {}", accountEntity.getId());
                logger.info("-------------------------------------------------------------------");

                return (Account) utils.setJsonStringToObject(accountEntityJSON.toString(),
                        Account.class);
            } else {
                logger.info("-------------------------------------------------------------------");
                logger.info("Account does not exists, proceed to create account");
                logger.info("-------------------------------------------------------------------");
                return null;
            }
        } catch (Exception e) {
            logger.error("-------------------------------------------------------------------");
            logger.error("Error fetching account : {}", e.getMessage());
            logger.error("-------------------------------------------------------------------");
            return null;
        }
    }

    @Override
    public Account GetAccountByClientId(String clientId) {
        try {
            AccountEntity accountEntity = accountRepository.findTopByClientId(clientId);

            if (accountEntity != null) {
                JSONObject accountEntityJSON = new JSONObject(accountEntity);

                logger.info("-------------------------------------------------------------------");
                logger.info("Account exists with id : {}", accountEntity.getId());
                logger.info("-------------------------------------------------------------------");

                return (Account) utils.setJsonStringToObject(accountEntityJSON.toString(),
                        Account.class);
            } else {
                logger.info("-------------------------------------------------------------------");
                logger.info("Account does not exists, proceed to create account");
                logger.info("-------------------------------------------------------------------");
                return null;
            }
        } catch (Exception e) {
            logger.error("-------------------------------------------------------------------");
            logger.error("Error fetching account : {}", e.getMessage());
            logger.error("-------------------------------------------------------------------");
            return null;
        }
    }

    @Override
    public ResponseEntity<?> GetAllAccounts() {
        try {
            List<AccountEntity> accountEntityList = accountRepository.findAll();

            List<Account> accountList =  utils.convertEntityListToDTOList(accountEntityList);

            logger.info("-------------------------------------------------------------------");
            logger.info("Returned records: {}", accountList.size());
            logger.info("-------------------------------------------------------------------");

            return ResponseEntity.ok().body(accountList);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


}
