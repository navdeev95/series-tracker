package io.github.nikoir.seriesparser.dto.response.movielab.search;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Pagination(
        @JsonProperty("page") Integer page,
        @JsonProperty("pages") Integer pages,
        @JsonProperty("on_page") Integer onPage,
        @JsonProperty("results") Integer results
) {}
