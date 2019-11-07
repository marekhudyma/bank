package com.marekhudyma.bank.domain.id;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class BalanceChangeId implements Serializable {

    private final UUID value;

    public BalanceChangeId() {
        this.value = UUID.randomUUID();
    }

    public BalanceChangeId(UUID value) {
        this.value = value;
    }

    public BalanceChangeId(String value) {
        this.value = UUID.fromString(value);
    }

    public static BalanceChangeId random() {
        return new BalanceChangeId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Converter
    public static class DbConverter implements AttributeConverter<BalanceChangeId, String> {

        @Override
        public String convertToDatabaseColumn(BalanceChangeId id) {
            return id.toString();
        }

        @Override
        public BalanceChangeId convertToEntityAttribute(String value) {
            return new BalanceChangeId(value);
        }
    }

}