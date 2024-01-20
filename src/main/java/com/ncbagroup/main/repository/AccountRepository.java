package com.ncbagroup.main.repository;

import com.ncbagroup.main.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    AccountEntity findTopById(Long accountId);
    AccountEntity findTopByClientId(String clientId);

    List<AccountEntity> findAll();


}
