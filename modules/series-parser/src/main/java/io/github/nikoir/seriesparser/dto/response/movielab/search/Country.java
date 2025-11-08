package io.github.nikoir.seriesparser.dto.response.movielab.search;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Country(
        @JsonProperty("id") Integer id,
        @JsonProperty("name") String name
) {}
