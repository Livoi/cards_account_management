package com.ncbagroup.main.model.request;

import lombok.Data;

@Data
public class Card {
    private Long id;
    private String alias;
    private Long accountId;
    private String cardType;

    public boolean isValid() {
        return alias != null && !alias.isEmpty() &&
                accountId != null &&
                cardType !=null && !cardType.isEmpty();
    }
}
