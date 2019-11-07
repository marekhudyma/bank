package com.marekhudyma.bank.domain;

import com.marekhudyma.bank.domain.id.AccountId;
import com.marekhudyma.bank.domain.id.BalanceChangeId;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BalanceChangeTestBuilder extends BalanceChange.BalanceChangeBuilder {

    private final int seed;

    public BalanceChange.BalanceChangeBuilder withTestDefaults() {
        return BalanceChange.builder()
                .id(new BalanceChangeId(new UUID(0, seed)))
                .accountId(new AccountId(new UUID(0, seed)))
                .amount(BigDecimal.valueOf(seed, 2));
    }

}