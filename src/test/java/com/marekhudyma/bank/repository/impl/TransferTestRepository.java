package com.marekhudyma.bank.repository.impl;

import com.marekhudyma.bank.domain.Transfer;
import com.marekhudyma.bank.domain.id.BalanceChangeId;
import com.marekhudyma.bank.domain.id.TransferId;
import io.micronaut.data.annotation.Repository;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import lombok.RequiredArgsConstructor;


@Repository
@RequiredArgsConstructor
public class TransferTestRepository {

    private final EntityManager entityManager;

    public Optional<Transfer> findById(TransferId transferId) {
        return Optional.ofNullable(entityManager.find(Transfer.class, transferId));
    }

    public List<Transfer> findByDebtorChangeIdAndCreditorChangeId(BalanceChangeId debtorChangeId,
                                                                  BalanceChangeId creditorChangeId) {
        Query query = entityManager.createNativeQuery(
                "SELECT * FROM transfers WHERE debtor_change_id = :debtorChangeId AND creditor_change_id = :creditorChangeId", Transfer.class)
                .setParameter("debtorChangeId", debtorChangeId.getValue())
                .setParameter("creditorChangeId", creditorChangeId.getValue());
        return query.getResultList();
    }

}