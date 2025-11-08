package io.github.nikoir.seriesparser.dto.response.movielab.search;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Ratings(
        @JsonProperty("kinopoisk") RatingInfo kinopoisk,
        @JsonProperty("imdb") RatingInfo imdb,
        @JsonProperty("mlab") RatingInfo mlab
) {}
