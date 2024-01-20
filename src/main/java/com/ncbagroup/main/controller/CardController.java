package com.ncbagroup.main.controller;

import com.ncbagroup.main.entity.CardEntity;
import com.ncbagroup.main.model.request.Card;
import com.ncbagroup.main.repository.CardRepository;
import com.ncbagroup.main.service.AccountServiceInterface;
import com.ncbagroup.main.service.CardServiceInterface;
import com.ncbagroup.main.util.Utils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/")
public class CardController {

    private static final Logger logger = LoggerFactory.getLogger(CardController.class);

    private CardRepository cardRepository;
    private CardServiceInterface cardServiceInterface;
    private AccountServiceInterface accountServiceInterface;
    private Utils utils;

    public CardController(CardRepository cardRepository, CardServiceInterface cardServiceInterface, AccountServiceInterface accountServiceInterface, Utils utils) {
        this.cardRepository = cardRepository;
        this.cardServiceInterface = cardServiceInterface;
        this.accountServiceInterface = accountServiceInterface;
        this.utils = utils;
    }


    // Create a new card
    @PostMapping(path = "create-card", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCard(@RequestBody Card card) throws Exception {
        // Validate the card
        if (!card.isValid()) {
            return ResponseEntity.badRequest().body(utils.convertMapToJson(utils.buildCardValidationErrorResponse(card)));
        }

        // Check if the associated account exists
        if (accountServiceInterface.GetAccount(card.getAccountId()) ==null) {
            return ResponseEntity.badRequest().body("Invalid request: Account does not exist.");
        }

        JSONObject incomingRequestJSON = new JSONObject(card);
        logger.info("-------------------------------------------------------------------");
        logger.info("Incoming request: \n{}", incomingRequestJSON);
        logger.info("-------------------------------------------------------------------");

        return cardServiceInterface.CreateNewCard(card);

    }

    // Get a card by ID
    @GetMapping("/{cardId}")
    public ResponseEntity<?> getCard(@PathVariable String cardId) {

        if (cardId.isEmpty()) {
            logger.error("-------------------------------------------------------------------");
            logger.error("CardEntity Id cannot be null or empty");
            logger.error("-------------------------------------------------------------------");
            return ResponseEntity.badRequest().body("CardEntity Id cannot be null or empty");
        }

        logger.info("-------------------------------------------------------------------");
        logger.info("Incoming request - CardEntity Id: \n{}", cardId);
        logger.info("-------------------------------------------------------------------");

        return cardServiceInterface.GetCard(cardId);
    }

    // Update a card by ID
//    @PutMapping("/{cardId}")
//    public ResponseEntity<Card> updateCard(@PathVariable String cardId, @RequestBody Card updatedCard) {
//        Optional<Card> existingCard = cardRepository.findById(cardId);
//        if (existingCard.isPresent()) {
//            Card card = existingCard.get();
//            card.setAlias(updatedCard.getAlias());
//            cardRepository.save(card);
//            return new ResponseEntity<>(card, HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }

    // Delete a card by ID
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(@PathVariable String cardId) {
        if (cardRepository.existsById(cardId)) {
            cardRepository.deleteById(cardId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // Get all cards associated with an account
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<CardEntity>> getCardsByAccountId(@PathVariable String accountId) {
        List<CardEntity> cards = cardRepository.findByAccountId(accountId);
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }
}
