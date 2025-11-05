package io.github.nikoir.seriesparser.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.github.nikoir.seriesparser.response.movielab.ContentType;

import java.io.IOException;

public class ContentTypeDeserializer extends JsonDeserializer<ContentType> {
    @Override
    public ContentType deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        String value = p.getValueAsString();
        return ContentType.fromApiValue(value);
    }
}