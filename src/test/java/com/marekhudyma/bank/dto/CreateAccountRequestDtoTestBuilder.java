package com.marekhudyma.bank.dto;

import lombok.RequiredArgsConstructor;

import static java.lang.String.format;

@RequiredArgsConstructor
public class CreateAccountRequestDtoTestBuilder extends CreateAccountRequestDto.CreateAccountRequestDtoBuilder {

    private final int seed;

    public CreateAccountRequestDto.CreateAccountRequestDtoBuilder withTestDefaults() {
        return CreateAccountRequestDto.builder()
                .owner(OwnerDto.builder()
                        .firstName(format("firstName.%d", seed))
                        .lastName(format("lastName.%d", seed))
                        .build());
    }

}
