package com.marekhudyma.bank.controller;

import com.marekhudyma.bank.domain.Account;
import com.marekhudyma.bank.dto.AccountResponseDto;
import com.marekhudyma.bank.dto.OwnerDto;
import javax.inject.Singleton;


@Singleton
public class AccountToAccountResponseConverter {

    public AccountResponseDto convert(Account account) {
        return AccountResponseDto.builder()
                .id(account.getId())
                .bankBalance(account.getBankBalance())
                .owner(OwnerDto.builder()
                        .firstName(account.getFirstName())
                        .lastName(account.getLastName())
                        .build())
                .build();
    }

}
