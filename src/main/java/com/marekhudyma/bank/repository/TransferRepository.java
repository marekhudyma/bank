package com.marekhudyma.bank.repository;

import com.marekhudyma.bank.domain.Transfer;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.GenericRepository;
import java.util.UUID;
import javax.persistence.EntityManager;
import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor
public class TransferRepository implements GenericRepository<Transfer, UUID> {

    private final EntityManager entityManager;

    public Transfer save(Transfer transfer) {
        entityManager.persist(transfer);
        return transfer;
    }

}