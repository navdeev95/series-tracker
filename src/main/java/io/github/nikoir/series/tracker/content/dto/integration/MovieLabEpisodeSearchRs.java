package io.github.nikoir.series.tracker.content.dto.integration;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import java.util.List;
import java.util.Map;

@Builder
public record MovieLabEpisodeSearchRs(
        Result result
) {
    @Builder
    public record Result(
            @JsonProperty("kinopoisk_id")
            Integer kinopoiskId,
            String type,
            Integer year,
            String titleRu,
            String titleEn,
            String description,
            String poster,
            String trailer,
            String rating,
            Ratings ratings,
            List<Genre> genres,
            List<Country> countries,
            List<Person> actors,
            List<Person> directors,
            Player player,
            String age,
            Integer duration,
            List<Object> similars,
            List<Object> facts,
            List<Object> trailers,

            @JsonProperty("serial_episodes")
            Map<String, Season> serialEpisodes,
            Reactions reactions,

            @JsonProperty("is_favorite")
            Boolean isFavorite,

            @JsonProperty("user_reaction")
            Object userReaction,
            List<Object> history,

            @JsonProperty("is_subscribed")
            Boolean isSubscribed,

            @JsonProperty("related_collections")
            List<Object> relatedCollections
    ) {}

    @Builder
    public record Ratings(
            Rating kinopoisk,
            Rating imdb,
            Rating mlab
    ) {}

    @Builder
    public record Rating(
            Double rating,
            Integer votes
    ) {}

    @Builder
    public record Genre(
            String id,
            String name
    ) {}

    @Builder
    public record Country(
            String id,
            String name
    ) {}

    @Builder
    public record Person(
            Integer id,
            String name,

            @JsonProperty("name_orig")
            String nameOrig,
            String poster,
            String number
    ) {}

    @Builder
    public record Player(
            String token,

            @JsonProperty("iframe_url")
            String iframeUrl,
            Object block,
            String translator,

            @JsonProperty("translator_id")
            Integer translatorId,
            String quality
    ) {}

    @Builder
    public record Season(
            @JsonProperty("season_number")
            Integer seasonNumber,

            @JsonProperty("episodes_count")
            Integer episodesCount,
            Map<String, Episode> episodes
    ) {}

    @Builder
    public record Episode(
            Integer episode,
            Map<String, Translation> translations
    ) {}

    @Builder
    public record Translation(
            String name,
            String hash,

            @JsonProperty("translation_id")
            Integer translationId
    ) {}

    @Builder
    public record Reactions(
            Integer fire,

            @JsonProperty("heart_eyes")
            Integer heartEyes,
            Integer rofl,
            Integer sad,
            Integer amazed,
            Integer fear,
            Integer sleeping,
            Integer angry,

            @JsonProperty("exploding_head")
            Integer explodingHead,
            Integer confused,
            Integer heart,
            Integer poop,
            Integer arm,
            Integer cool
    ) {}
}
