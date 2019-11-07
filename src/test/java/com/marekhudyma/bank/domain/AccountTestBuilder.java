package com.marekhudyma.bank.domain;

import com.marekhudyma.bank.domain.id.AccountId;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

import static java.lang.String.format;


@RequiredArgsConstructor
public class AccountTestBuilder extends Account.AccountBuilder {

    private final int seed;

    public Account.AccountBuilder withTestDefaults() {
        return Account.builder()
                .id(new AccountId(new UUID(0, seed)))
                .firstName(format("firstName.%d", seed))
                .lastName(format("lastName.%d", seed))
                .bankBalance(BigDecimal.valueOf(seed, 2));
    }

}