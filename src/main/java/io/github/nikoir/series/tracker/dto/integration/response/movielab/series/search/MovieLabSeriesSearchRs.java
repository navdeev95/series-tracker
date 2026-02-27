package io.github.nikoir.series.tracker.dto.integration.response.movielab.series.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MovieLabSeriesSearchRs(
        @JsonProperty("results")
        List<SearchResult> results,

        @JsonProperty("pagination")
        Pagination pagination
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record SearchResult(
            @JsonProperty("kinopoisk_id")
            Integer kinopoiskId,

            @JsonProperty("type")
            String type,

            @JsonProperty("year")
            Integer year,

            @JsonProperty("title_ru")
            String titleRu,

            @JsonProperty("title_en")
            String titleEn,

            @JsonProperty("description")
            String description,

            @JsonProperty("poster")
            String poster,

            @JsonProperty("trailer")
            String trailer,

            @JsonProperty("rating")
            String rating,

            @JsonProperty("ratings")
            Ratings ratings,

            @JsonProperty("genres")
            List<Genre> genres,

            @JsonProperty("countries")
            List<Country> countries,

            @JsonProperty("player")
            PlayerInfo player,

            @JsonProperty("age")
            Integer age,

            @JsonProperty("duration")
            Integer duration,

            @JsonProperty("is_subscribed")
            Boolean isSubscribed,

            @JsonProperty("is_favorite")
            Boolean isFavorite
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Ratings(
            @JsonProperty("kinopoisk")
            RatingInfo kinopoisk,

            @JsonProperty("imdb")
            RatingInfo imdb,

            @JsonProperty("mlab")
            RatingInfo mlab
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record RatingInfo(
            @JsonProperty("rating")
            Object rating, // Double или null

            @JsonProperty("votes")
            Object votes // Integer или null
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Genre(
            @JsonProperty("id")
            Integer id,

            @JsonProperty("name")
            String name
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Country(
            @JsonProperty("id")
            Integer id,

            @JsonProperty("name")
            String name
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record PlayerInfo(
            @JsonProperty("token")
            String token,

            @JsonProperty("iframe_url")
            String iframeUrl,

            @JsonProperty("block")
            Object block, // null или другой тип

            @JsonProperty("translator")
            String translator,

            @JsonProperty("translator_id")
            Integer translatorId,

            @JsonProperty("quality")
            String quality
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Pagination(
            @JsonProperty("page")
            Integer page,

            @JsonProperty("pages")
            Integer pages,

            @JsonProperty("on_page")
            Integer onPage,

            @JsonProperty("results")
            Integer results
    ) {}
}