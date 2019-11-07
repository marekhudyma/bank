package com.marekhudyma.bank.dto.json;


import com.marekhudyma.bank.domain.id.AccountId;

public class AccountIdDeserializer extends FromStringJsonDeserializer<AccountId> {

    @Override
    protected AccountId construct(String value) {
        return new AccountId(value);
    }
}
