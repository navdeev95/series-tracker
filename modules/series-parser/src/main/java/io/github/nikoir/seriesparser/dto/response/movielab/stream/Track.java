package io.github.nikoir.seriesparser.dto.response.movielab.stream;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Track(
        @JsonProperty("kind")
        String kind,

        @JsonProperty("src")
        String src,

        @JsonProperty("srlang")
        String language,

        @JsonProperty("label")
        String label
) {}