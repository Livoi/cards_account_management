package com.ncbagroup.main.repository;

import com.ncbagroup.main.entity.AccountEntity;
import com.ncbagroup.main.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CardRepository extends JpaRepository<CardEntity, String> {
    // Custom query method to find cards by account ID
    List<CardEntity> findByAccountId(String accountId);


}
