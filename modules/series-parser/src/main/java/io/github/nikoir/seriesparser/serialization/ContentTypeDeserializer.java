package io.github.nikoir.seriesparser.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.github.nikoir.seriesparser.dto.response.lumex.ContentType;

import java.io.IOException;

public class ContentTypeDeserializer extends JsonDeserializer<ContentType> {
    @Override
    public ContentType deserialize(JsonParser jsonParser, DeserializationContext ctxt)
            throws IOException {
        String value = jsonParser.getValueAsString();
        return ContentType.fromApiValue(value);
    }
}