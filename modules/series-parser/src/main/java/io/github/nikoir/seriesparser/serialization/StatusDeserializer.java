package io.github.nikoir.seriesparser.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.github.nikoir.seriesparser.dto.response.kinopoisk.Status;

import java.io.IOException;

public class StatusDeserializer extends JsonDeserializer<Status> {
    @Override
    public Status deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String value = jsonParser.getValueAsString();
        return Status.fromApiValue(value);
    }
}
