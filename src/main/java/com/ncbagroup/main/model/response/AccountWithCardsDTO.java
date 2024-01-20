package com.ncbagroup.main.model.response;

import com.ncbagroup.main.model.request.Card;
import lombok.Data;

import java.util.List;

@Data
public class AccountWithCardsDTO {
    private Long id;
    private String iban;
    private String bicSwift;
    private String clientId;
    private List<Card> cards;
}
