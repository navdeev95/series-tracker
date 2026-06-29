package io.github.nikoir.series.tracker.content.dto.integration;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TMDBSeriesSearchRs(
        @JsonProperty("page")
        Integer page,

        @JsonProperty("results")
        List<Result> results,

        @JsonProperty("total_pages")
        Integer totalPages,

        @JsonProperty("total_results")
        Integer totalResults
) {
    public record Result(
            @JsonProperty("id")
            Integer id,

            @JsonProperty("name")
            String name,

            @JsonProperty("original_name")
            String originalName,

            @JsonProperty("original_language")
            String originalLanguage,

            @JsonProperty("overview")
            String overview,

            @JsonProperty("first_air_date")
            String firstAirDate,

            @JsonProperty("poster_path")
            String posterPath,

            @JsonProperty("backdrop_path")
            String backdropPath,

            @JsonProperty("genre_ids")
            List<Integer> genreIds,

            @JsonProperty("origin_country")
            List<String> originCountry,

            @JsonProperty("adult")
            Boolean adult,

            @JsonProperty("popularity")
            Double popularity,

            @JsonProperty("vote_average")
            Integer voteAverage,

            @JsonProperty("vote_count")
            Integer voteCount,

            @JsonProperty("softcore")
            Boolean softcore
    ) {
    }
}