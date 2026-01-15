package io.github.nikoir.series.tracker.dto.api.response.kinopoisk;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.github.nikoir.series.tracker.serialization.StatusDeserializer;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

public record KinopoiskSeriesSearchRs(
        @JsonProperty("docs")
        List<Doc> docs,

        @JsonProperty("total")
        Integer total,

        @JsonProperty("page")
        Integer page,

        @JsonProperty("pages")
        Integer pages,

        @JsonProperty("limit")
        Integer limit
) {
        public record Doc(@JsonProperty("id")
                          Integer id,

                          @JsonProperty("name")
                          String name,

                          @JsonProperty("alternativeName")
                          String alternativeName,

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

    @RequiredArgsConstructor
    public enum Status {
        FILMING("filming"),
        PRE_PRODUCTION("pre-production"),
        COMPLETED("completed"),
        ANNOUNCED("announced"),
        POST_PRODUCTION("post-production"),
        DELETED("deleted");

        final String name;

        public static Status fromApiValue(String apiValue) {
            if (apiValue == null) {
                return null;
            }

            for (Status status : Status.values()) {
                if (status.name.equals(apiValue)) {
                    return status;
                }
            }

            throw new IllegalArgumentException("Unknown content type: " + apiValue);
        }
    }
}