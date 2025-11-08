package io.github.nikoir.seriesparser.dto.response.kinopoisk;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.nikoir.seriesparser.serialization.StatusDeserializer;
import java.util.Map;

public record Doc(@JsonProperty("id")
                  Integer id,

                  @JsonProperty("name")
                  String name,

                  @JsonProperty("enName")
                  String enName,

                  @JsonDeserialize(using = StatusDeserializer.class)
                  Status status,

                  @JsonProperty("year")
                  Integer year,

                  @JsonProperty("poster")
                  Image poster,

                  @JsonProperty("externalId")
                  Map<String, String> externalId,

                  @JsonProperty("isSeries")
                  Boolean isSeries) {
}
