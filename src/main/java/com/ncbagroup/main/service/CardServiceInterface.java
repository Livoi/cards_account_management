package com.ncbagroup.main.service;

import com.ncbagroup.main.model.request.Card;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface CardServiceInterface {

    ResponseEntity<?> CreateNewCard(Card card);
    ResponseEntity<?> GetCard(String cardId);
    ResponseEntity<?> UpdateCard(Long cardId, Card card);
    ResponseEntity<?> DeleteCard(String cardId);
    ResponseEntity<?> GetAllCardsAssociatedWithAccount(Long account);
}
