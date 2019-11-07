package com.marekhudyma.bank.domain.id;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class TransferId implements Serializable {

    private final UUID value;

    public TransferId() {
        this.value = UUID.randomUUID();
    }

    public TransferId(UUID value) {
        this.value = value;
    }

    public TransferId(String accountId) {
        this.value = UUID.fromString(accountId);
    }

    public static TransferId random() {
        return new TransferId(UUID.randomUUID());
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Converter
    public static class DbConverter implements AttributeConverter<TransferId, String> {

        @Override
        public String convertToDatabaseColumn(TransferId id) {
            return id.toString();
        }

        @Override
        public TransferId convertToEntityAttribute(String value) {
            return new TransferId(value);
        }
    }

}