package io.github.nikoir.series.tracker.content.dto.integration;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TMDBSeriesInfoRs(
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

        @JsonProperty("tagline")
        String tagline,

        @JsonProperty("first_air_date")
        String firstAirDate,

        @JsonProperty("last_air_date")
        String lastAirDate,

        @JsonProperty("poster_path")
        String posterPath,

        @JsonProperty("backdrop_path")
        String backdropPath,

        @JsonProperty("adult")
        Boolean adult,

        @JsonProperty("in_production")
        Boolean inProduction,

        @JsonProperty("popularity")
        Double popularity,

        @JsonProperty("vote_average")
        Double voteAverage,

        @JsonProperty("vote_count")
        Integer voteCount,

        @JsonProperty("number_of_episodes")
        Integer numberOfEpisodes,

        @JsonProperty("number_of_seasons")
        Integer numberOfSeasons,

        @JsonProperty("status")
        String status,

        @JsonProperty("type")
        String type,

        @JsonProperty("homepage")
        String homepage,

        @JsonProperty("softcore")
        Boolean softcore,

        @JsonProperty("created_by")
        List<Creator> createdBy,

        @JsonProperty("genres")
        List<Genre> genres,

        @JsonProperty("languages")
        List<String> languages,

        @JsonProperty("networks")
        List<Network> networks,

        @JsonProperty("production_companies")
        List<ProductionCompany> productionCompanies,

        @JsonProperty("production_countries")
        List<ProductionCountry> productionCountries,

        @JsonProperty("spoken_languages")
        List<SpokenLanguage> spokenLanguages,

        @JsonProperty("origin_country")
        List<String> originCountry,

        @JsonProperty("seasons")
        List<Season> seasons,

        @JsonProperty("last_episode_to_air")
        TMDBEpisodeInfoRs lastEpisodeToAir,

        @JsonProperty("next_episode_to_air")
        TMDBEpisodeInfoRs nextEpisodeToAir
) {
    public record Creator(
            @JsonProperty("id")
            Integer id,

            @JsonProperty("credit_id")
            String creditId,

            @JsonProperty("name")
            String name,

            @JsonProperty("original_name")
            String originalName,

            @JsonProperty("gender")
            Integer gender,

            @JsonProperty("profile_path")
            String profilePath
    ) {}

    public record Genre(
            @JsonProperty("id")
            Integer id,

            @JsonProperty("name")
            String name
    ) {}

    public record Network(
            @JsonProperty("id")
            Integer id,

            @JsonProperty("logo_path")
            String logoPath,

            @JsonProperty("name")
            String name,

            @JsonProperty("origin_country")
            String originCountry
    ) {}

    public record ProductionCompany(
            @JsonProperty("id")
            Integer id,

            @JsonProperty("logo_path")
            String logoPath,

            @JsonProperty("name")
            String name,

            @JsonProperty("origin_country")
            String originCountry
    ) {}

    public record ProductionCountry(
            @JsonProperty("iso_3166_1")
            String iso3166_1,

            @JsonProperty("name")
            String name
    ) {}

    public record SpokenLanguage(
            @JsonProperty("english_name")
            String englishName,

            @JsonProperty("iso_639_1")
            String iso639_1,

            @JsonProperty("name")
            String name
    ) {}

    public record Season(
            @JsonProperty("id")
            Integer id,

            @JsonProperty("name")
            String name,

            @JsonProperty("overview")
            String overview,

            @JsonProperty("air_date")
            String airDate,

            @JsonProperty("poster_path")
            String posterPath,

            @JsonProperty("season_number")
            Integer seasonNumber,

            @JsonProperty("episode_count")
            Integer episodeCount,

            @JsonProperty("vote_average")
            Double voteAverage
    ) {}
}
