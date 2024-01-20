package com.ncbagroup.main.service;

import com.ncbagroup.main.model.request.Account;
import com.ncbagroup.main.model.request.Card;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface AccountServiceInterface {
    ResponseEntity<String> CreateAccountWithCards(Account account, List<Card> cardList);
    ResponseEntity<?> CreateAccount(Account account);
    Account GetAccount(Long accountId);

    Account GetAccountByClientId(String clientId);
    ResponseEntity<?> GetAllAccounts();
}
