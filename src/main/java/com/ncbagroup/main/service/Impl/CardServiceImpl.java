package com.ncbagroup.main.service.Impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ncbagroup.main.entity.AccountEntity;
import com.ncbagroup.main.entity.CardEntity;
import com.ncbagroup.main.model.request.Account;
import com.ncbagroup.main.model.request.Card;
import com.ncbagroup.main.model.response.AccountWithCardsDTO;
import com.ncbagroup.main.repository.AccountRepository;
import com.ncbagroup.main.repository.CardRepository;
import com.ncbagroup.main.service.CardServiceInterface;
import com.ncbagroup.main.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardServiceImpl implements CardServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(CardServiceImpl.class);

    private Utils utils;
    private final CardRepository cardRepository;
    private AccountRepository accountRepository;

    public CardServiceImpl(Utils utils,
                           CardRepository cardRepository, AccountRepository accountRepository) {
        this.utils = utils;
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    @Transactional
    public ResponseEntity<?> CreateNewCard(Card card) {
        try {
            AccountEntity accountEntity = accountRepository.findById(card.getAccountId()).orElse(null);

            if (accountEntity == null) {
                // Handle the case where the account does not exist
                // Return an appropriate response, e.g., 404 Not Found
                return ResponseEntity.notFound().build();
            }

            CardEntity cardEntity = utils.convertDtoToEntity(card, CardEntity.class);

            // Associate the card with the account
            accountEntity.addCard(cardEntity);

            // Save the card to the database
            CardEntity savedCardEntity = cardRepository.save(cardEntity);


            // Create a DTO for the response
            AccountWithCardsDTO accountWithCardsDTO = new AccountWithCardsDTO();
            accountWithCardsDTO.setId(accountEntity.getId());
            accountWithCardsDTO.setIban(accountEntity.getIban());
            accountWithCardsDTO.setBicSwift(accountEntity.getBicSwift());
            accountWithCardsDTO.setClientId(accountEntity.getClientId());

            // Retrieve the associated cards (including the newly saved card)
            List<Card> cardDTOs = accountEntity.getCards().stream()
                    .map(this::mapToCardDTO)
                    .collect(Collectors.toList());

            accountWithCardsDTO.setCards(cardDTOs);

            // Log and return the response
            ResponseEntity<?> responseEntity = ResponseEntity.ok().body(accountWithCardsDTO);
            utils.logResponseEntity(responseEntity, "create new card", false);
            return responseEntity;

        } catch (Exception e) {
            // Handle exceptions or log errors
            ResponseEntity<?> responseEntity = ResponseEntity.internalServerError().body(e.getMessage());
            try {
                utils.logResponseEntity(responseEntity, "create new card", true);
            } catch (JsonProcessingException ex) {
                throw new RuntimeException(ex);
            }
            return responseEntity;
        }
    }

    private Card mapToCardDTO(CardEntity cardEntity) {
        Card cardDTO = new Card();
        cardDTO.setId(cardEntity.getId());
        cardDTO.setAlias(cardEntity.getAlias());
        cardDTO.setCardType(cardEntity.getCardType());

        // Map other card attributes as needed

        return cardDTO;
    }

    @Override
    public ResponseEntity<?> GetCard(String cardId) {
        return null;
    }

    @Override
    public ResponseEntity<?> UpdateCard(Card card) {
        return null;
    }

    @Override
    public ResponseEntity<?> DeleteCard(String cardId) {
        return null;
    }

    @Override
    public ResponseEntity<?> GetAllCardsAssociatedWithAccount(String account) {
        return null;
    }
}
