package com.marekhudyma.bank.repository;

import com.marekhudyma.bank.domain.Account;
import com.marekhudyma.bank.domain.id.AccountId;
import io.micronaut.data.annotation.Repository;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class AccountRepository {

    private final EntityManager entityManager;

    public Account save(Account account) {
        entityManager.persist(account);
        return account;
    }

    public Optional<Account> findByIdAndLockNoWait(AccountId accountId) {
        Query query = entityManager.createNativeQuery(
                "SELECT * FROM accounts WHERE id = :accountId FOR UPDATE NOWAIT", Account.class)
                .setParameter("accountId", accountId.getValue());

        return get(query.getResultList());
    }

    private Optional<Account> get(List<Account> accounts) {
        return accounts.stream().findFirst();
    }

}