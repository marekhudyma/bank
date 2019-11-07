package com.marekhudyma.bank.domain.id;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class AccountId implements Serializable {

    private final UUID value;

    public AccountId() {
        this.value = UUID.randomUUID();
    }

    public AccountId(UUID value) {
        this.value = value;
    }

    public AccountId(String value) {
        this.value = UUID.fromString(value);
    }

    public static AccountId random() {
        return new AccountId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Converter
    public static class DbConverter implements AttributeConverter<AccountId, String> {

        @Override
        public String convertToDatabaseColumn(AccountId id) {
            return id.toString();
        }

        @Override
        public AccountId convertToEntityAttribute(String value) {
            return new AccountId(value);
        }
    }

}