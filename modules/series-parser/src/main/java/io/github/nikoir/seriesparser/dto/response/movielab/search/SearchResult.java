package io.github.nikoir.seriesparser.dto.response.movielab.search;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SearchResult(
        @JsonProperty("kinopoisk_id") Integer kinopoiskId,
        @JsonProperty("type") String type,
        @JsonProperty("year") Integer year,
        @JsonProperty("title_ru") String titleRu,
        @JsonProperty("title_en") String titleEn,
        @JsonProperty("description") String description,
        @JsonProperty("poster") String poster,
        @JsonProperty("trailer") String trailer,
        @JsonProperty("rating") Double rating,
        @JsonProperty("ratings") Ratings ratings,
        @JsonProperty("genres") List<Genre> genres,
        @JsonProperty("countries") List<Country> countries,
        @JsonProperty("player") PlayerInfo player,
        @JsonProperty("age") Integer age,
        @JsonProperty("duration") Integer duration,
        @JsonProperty("is_subscribed") Boolean isSubscribed,
        @JsonProperty("is_favorite") Boolean isFavorite
) {}
