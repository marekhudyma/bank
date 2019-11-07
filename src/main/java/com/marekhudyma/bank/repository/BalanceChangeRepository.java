package com.marekhudyma.bank.repository;

import com.marekhudyma.bank.domain.BalanceChange;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.GenericRepository;
import java.util.UUID;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor
public class BalanceChangeRepository implements GenericRepository<BalanceChange, UUID> {

    private final EntityManager entityManager;

    public BalanceChange save(BalanceChange balanceChange) {
        entityManager.persist(balanceChange);
        return balanceChange;
    }
}