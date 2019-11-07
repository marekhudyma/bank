package com.marekhudyma.bank.repository.impl;

import com.marekhudyma.bank.domain.Account;
import com.marekhudyma.bank.domain.id.AccountId;
import io.micronaut.data.annotation.Repository;
import java.util.Optional;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor
public class AccountTestRepository {

    private final EntityManager entityManager;

    public Optional<Account> findById(AccountId accountId) {
        return Optional.ofNullable(entityManager.find(Account.class, accountId));
    }

}