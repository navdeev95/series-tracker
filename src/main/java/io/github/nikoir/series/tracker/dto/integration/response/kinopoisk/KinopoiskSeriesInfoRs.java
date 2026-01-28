package io.github.nikoir.series.tracker.dto.integration.response.kinopoisk;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record KinopoiskSeriesInfoRs(
        Long id,
        Map<String, String> externalId,
        String name,
        String alternativeName,
        String enName,
        List<Name> names,
        String type,
        Integer typeNumber,
        Integer year,
        String description,
        String shortDescription,
        String slogan,
        String status,
        List<Fact> facts,
        Rating rating,
        Votes votes,
        Integer movieLength,
        String ratingMpaa,
        Integer ageRating,
        Logo logo,
        Image poster,
        Image backdrop,
        Videos videos,
        List<Genre> genres,
        List<Country> countries,
        List<Person> persons,
        ReviewInfo reviewInfo,
        List<SeasonInfo> seasonsInfo,
        Budget budget,
        Fees fees,
        Premiere premiere,
        List<SimilarMovie> similarMovies,
        List<SimilarMovie> sequelsAndPrequels,
        Watchability watchability,
        List<ReleaseYear> releaseYears,
        Integer top10,
        Integer top250,
        Boolean ticketsOnSale,
        Integer totalSeriesLength,
        Integer seriesLength,
        Boolean isSeries,
        List<Audience> audience,
        List<String> lists,
        Networks networks,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        LocalDateTime updatedAt,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        LocalDateTime createdAt
) {

    public record Name(
            String name,
            String language,
            String type
    ) {}

    public record Fact(
            String value,
            String type,
            Boolean spoiler
    ) {}

    public record Rating(
            Double kp,
            Double imdb,
            Double tmdb,
            Double filmCritics,
            Double russianFilmCritics,
            Double await
    ) {}

    public record Votes(
            String kp,
            Long imdb,
            Long tmdb,
            Long filmCritics,
            Long russianFilmCritics,
            Long await
    ) {}

    public record Logo(
            String url
    ) {}

    public record Video(
            String url,
            String name,
            String site,
            Integer size,
            String type
    ) {}

    public record Videos(
            List<Video> trailers
    ) {}

    public record Genre(
            String name
    ) {}

    public record Country(
            String name
    ) {}

    public record Person(
            Long id,
            String photo,
            String name,
            String enName,
            String description,
            String profession,
            String enProfession
    ) {}

    public record ReviewInfo(
            Long count,
            Long positiveCount,
            String percentage
    ) {}

    public record SeasonInfo(
            Integer number,
            Integer episodesCount
    ) {}

    public record Budget(
            Long value,
            String currency
    ) {}

    public record Fee(
            Long value,
            String currency
    ) {}

    public record Fees(
            Fee world,
            Fee russia,
            Fee usa
    ) {}

    public record Premiere(
            String country,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            LocalDateTime world,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            LocalDateTime russia,
            String digital,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            LocalDateTime cinema,
            String bluray,
            String dvd
    ) {}

    public record SimilarMovie(
            Long id,
            String name,
            String enName,
            String alternativeName,
            String type,
            Image poster,
            Rating rating,
            Integer year
    ) {}

    public record WatchabilityItem(
            String name,
            Logo logo,
            String url
    ) {}

    public record Watchability(
            List<WatchabilityItem> items
    ) {}

    public record ReleaseYear(
            Integer start,
            Integer end
    ) {}

    public record Audience(
            Long count,
            String country
    ) {}

    public record Network(
            String name,
            Logo logo
    ) {}

    public record Networks(
            List<Network> items
    ) {}
}