package io.github.nikoir.seriesparser.dto.response.movielab.search;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RatingInfo(
        @JsonProperty("rating") Double rating,
        @JsonProperty("votes") Integer votes
) {}
