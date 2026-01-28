package io.github.nikoir.series.tracker.serialization;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import io.github.nikoir.series.tracker.dto.integration.response.kinopoisk.KinopoiskSeriesSearchRs;

import java.io.IOException;

public class StatusDeserializer extends JsonDeserializer<KinopoiskSeriesSearchRs.Status> {
    @Override
    public KinopoiskSeriesSearchRs.Status deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
        String value = jsonParser.getValueAsString();
        return KinopoiskSeriesSearchRs.Status.fromApiValue(value);
    }
}
