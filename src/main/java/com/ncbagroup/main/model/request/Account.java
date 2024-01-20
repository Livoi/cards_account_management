package com.ncbagroup.main.model.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.util.List;


@Data
public class Account {
    private Long id;
    private String iban;
    private String bicSwift;
    private String clientId;

    private List<Card> cards;

    public boolean isValid() {
        return isValidIban(iban) &&
                isValidBicSwift(bicSwift) &&
                clientId != null && !clientId.isEmpty();
    }

    private boolean isValidIban(String iban) {
        // Implement your IBAN validation logic
        // For simplicity, let's assume any non-null and non-empty string is valid
        return iban != null && !iban.isEmpty();
    }

    private boolean isValidBicSwift(String bicSwift) {
        // Implement your BIC/SWIFT validation logic
        // For simplicity, let's assume any non-null and non-empty string is valid
        return bicSwift != null && !bicSwift.isEmpty();
    }
}
