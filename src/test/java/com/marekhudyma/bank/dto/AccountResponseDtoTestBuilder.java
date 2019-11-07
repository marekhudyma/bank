package com.marekhudyma.bank.dto;

import com.marekhudyma.bank.domain.id.AccountId;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;

import static java.lang.String.format;

@RequiredArgsConstructor
public class AccountResponseDtoTestBuilder extends AccountResponseDto.AccountResponseDtoBuilder {

    private final int seed;

    public AccountResponseDto.AccountResponseDtoBuilder withTestDefaults() {
        return AccountResponseDto.builder()
                .id(new AccountId(new UUID(0, seed)))
                .bankBalance(BigDecimal.valueOf(seed, 2))
                .owner(OwnerDto.builder()
                        .firstName(format("firstName.%d", seed))
                        .lastName(format("lastName.%d", seed))
                        .build());
    }

}
