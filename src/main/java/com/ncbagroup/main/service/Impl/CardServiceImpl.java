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
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
            JSONObject incomingRequestJSON = new JSONObject(card);
            logger.info("-------------------------------------------------------------------");
            logger.info("Incoming request: \n{}", incomingRequestJSON);
            logger.info("-------------------------------------------------------------------");

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
    public ResponseEntity<?> UpdateCard(Long cardId, Card card) {

        try {
            Optional<CardEntity> existingCard = cardRepository.findById(cardId);
            if (existingCard.isPresent()) {
                CardEntity cardEntity = existingCard.get();
                cardEntity.setAlias(card.getAlias());
                cardEntity = cardRepository.save(cardEntity);

                Card cardResp = utils.convertEntityToDto(cardEntity, Card.class);

                cardResp.setAccountId(cardEntity.getAccount().getId());

                ResponseEntity<?> responseEntity = ResponseEntity.ok().body(cardResp);

                utils.logResponseEntity(responseEntity, "update card with id: "+ cardId, false);

                return responseEntity;
            } else {
                // If resource is not found, return a 404 response with a JSON body
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("id", cardId);
                responseBody.put("status", "Not Found");
                ResponseEntity<?> responseEntity = ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
                utils.logResponseEntity(responseEntity, "Failed to update card with id: "+ cardId , true);
                return responseEntity;
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEntity<?> DeleteCard(String cardId) {
        return null;
    }

    @Override
    public ResponseEntity<?> GetAllCardsAssociatedWithAccount(Long accountId) {

        List<CardEntity> cards = cardRepository.findByAccountId(accountId);

        List<Card> cardList = utils.convertCardEntityListToDTOList(cards);

        ResponseEntity<?> responseEntity = ResponseEntity.ok().body(cardList);

        return responseEntity;
    }
}
