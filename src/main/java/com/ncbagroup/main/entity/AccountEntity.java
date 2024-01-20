package com.ncbagroup.main.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@Table(name = "NCBA_ACCOUNTS")
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String iban;
    private String bicSwift;
    private String clientId;


    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore // Add this annotation to break the loop
    private List<CardEntity> cards = new ArrayList<>();

    // Add utility method to manage the bidirectional relationship
    public void addCard(CardEntity card) {
        cards.add(card);
        card.setAccount(this);
    }

    public void removeCard(CardEntity card) {
        cards.remove(card);
        card.setAccount(null);
    }

    @Override
    public String toString() {
        return "AccountEntity{" +
                "id=" + id +
                // Include other fields as needed...
                '}';
    }
}
