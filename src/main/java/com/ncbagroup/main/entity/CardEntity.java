package com.ncbagroup.main.entity;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;

@Data
@Entity
@Table(name = "NCBA_CARDS")
public class CardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Use GenerationType.IDENTITY for auto-incremented ID
    private Long id;
    private String alias;
    private String cardType;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private AccountEntity account;
}
