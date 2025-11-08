package io.github.nikoir.seriesparser.dto.response.movielab.search;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Genre(
        @JsonProperty("id") Integer id,
        @JsonProperty("name") String name
) {}
