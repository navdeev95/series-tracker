package io.github.nikoir.tracker.builder.domain;

import io.github.nikoir.tracker.content.domain.entity.Country;
import io.github.nikoir.tracker.content.domain.entity.ExternalIdSeries;
import io.github.nikoir.tracker.content.domain.entity.Season;
import io.github.nikoir.tracker.content.domain.entity.Series;
import io.github.nikoir.tracker.content.domain.entity.dictionary.DictExternalId;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Билдер для создания тестовых объектов Series
 */
public class SeriesBuilder {
    private String title;
    private String engTitle;
    private Integer totalSeasons;
    private Series.Status status;
    private Integer releaseYear;
    private String posterUrl;
    private String description;
    private Set<Country> countries;
    private List<ExternalIdSeries> externalIds;
    private List<Season> seasons;

    // Конструктор со значениями по умолчанию
    public SeriesBuilder() {
        this.title = "Default Series";
        this.engTitle = "Default Series";
        this.totalSeasons = 1;
        this.status = Series.Status.ANNOUNCED;
        this.releaseYear = 2024;
        this.countries = new HashSet<>();
        this.externalIds = new ArrayList<>();
        this.seasons = new ArrayList<>();
    }

    public SeriesBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public SeriesBuilder withEngTitle(String engTitle) {
        this.engTitle = engTitle;
        return this;
    }

    public SeriesBuilder withTotalSeasons(Integer totalSeasons) {
        this.totalSeasons = totalSeasons;
        return this;
    }

    public SeriesBuilder withStatus(Series.Status status) {
        this.status = status;
        return this;
    }

    public SeriesBuilder withReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
        return this;
    }

    public SeriesBuilder withPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
        return this;
    }

    public SeriesBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public SeriesBuilder withCountries(List<Country> countries) {
        this.countries = countries != null ? new HashSet<>(countries) : new HashSet<>();
        return this;
    }

    public SeriesBuilder withCountry(Country country) {
        if (this.countries == null) {
            this.countries = new HashSet<>();
        }
        this.countries.add(country);
        return this;
    }

    public SeriesBuilder withExternalId(DictExternalId externalId, String value) {
        this.externalIds.add(ExternalIdSeries.builder()
                        .externalId(externalId)
                        .value(value).build());

        return this;
    }

    public SeriesBuilder withSeasons(List<Season> seasons) {
        this.seasons = seasons != null ? new ArrayList<>(seasons) : new ArrayList<>();
        return this;
    }

    public SeriesBuilder withSeason(Season season) {
        if (this.seasons == null) {
            this.seasons = new ArrayList<>();
        }
        this.seasons.add(season);
        return this;
    }

    public SeriesBuilder withRandomSeasons(int count) {
        return withGeneratedSeasons(count, i -> new SeasonBuilder().withNumber(i + 1).build());
    }

    public SeriesBuilder withGeneratedSeasons(int count, Function<Integer, Season> seasonGenerator) {
        for (int i = 0; i < count; i++) {
            this.seasons.add(seasonGenerator.apply(i));
        }
        return this;
    }

    public Series build() {
        Series series = new Series();
        series.setTitle(title);
        series.setEngTitle(engTitle);
        series.setTotalSeasons(totalSeasons);
        series.setStatus(status);
        series.setReleaseYear(releaseYear);
        series.setPosterUrl(posterUrl);
        series.setDescription(description);
        series.setCountries(countries);
        series.setExternalIds(externalIds);
        series.setSeasons(seasons);

        // Устанавливаем обратную связь для сезонов
        if (seasons != null) {
            seasons.forEach(season -> season.setSeries(series));
        }

        if (externalIds != null) {
            externalIds.forEach(externalId -> externalId.setSeries(series));
        }

        return series;
    }
}