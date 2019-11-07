package com.marekhudyma.bank.repository.impl;

import com.marekhudyma.bank.domain.BalanceChange;
import com.marekhudyma.bank.domain.id.AccountId;
import com.marekhudyma.bank.domain.id.BalanceChangeId;
import io.micronaut.data.annotation.Repository;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class BalanceChangeTestRepository {

    private final EntityManager entityManager;

    public Optional<BalanceChange> findById(BalanceChangeId balanceChangeId) {
        return Optional.ofNullable(entityManager.find(BalanceChange.class, balanceChangeId));
    }

    public List<BalanceChange> findByAccountIdOrderByCreatedAtDesc(AccountId accountId) {
        Query query = entityManager.createNativeQuery(
                "SELECT * FROM balance_changes WHERE account_id = :accountId ORDER BY created_at DESC",
                BalanceChange.class)
                .setParameter("accountId", accountId.getValue());
        return query.getResultList();
    }

}