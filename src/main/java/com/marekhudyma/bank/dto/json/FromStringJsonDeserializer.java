package com.marekhudyma.bank.dto.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;

public abstract class FromStringJsonDeserializer<T> extends JsonDeserializer<T> {

    @Override
    public T deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        return construct(parser.getValueAsString());
    }

    abstract protected T construct(String value);
}
