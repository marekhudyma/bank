package com.marekhudyma.bank.domain;

import com.marekhudyma.bank.domain.id.BalanceChangeId;
import com.marekhudyma.bank.domain.id.TransferId;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TransferTestBuilder {

    private final int seed;

    public Transfer.TransferBuilder withTestDefaults() {
        return Transfer.builder()
                .id(new TransferId(new UUID(0, seed)))
                .creditorChangeId(new BalanceChangeId(new UUID(0, seed)))
                .debtorChangeId(new BalanceChangeId(new UUID(0, seed)));
    }

}