package io.github.nikoir.series.tracker.builder.domain;

import io.github.nikoir.series.tracker.content.domain.entity.Episode;
import io.github.nikoir.series.tracker.content.domain.entity.Season;
import io.github.nikoir.series.tracker.content.domain.entity.Series;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SeasonBuilder {
    private String name;
    private Integer number;
    private LocalDate releaseDate;
    private Series series;
    private List<Episode> episodes = new ArrayList<>();

    public SeasonBuilder() {
        // Значения по умолчанию
        this.number = 1;
        this.releaseDate = LocalDate.now();
        this.name = "Season " + number;
    }

    public SeasonBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public SeasonBuilder withNumber(Integer number) {
        this.number = number;
        if (this.name == null || this.name.equals("Season " + (this.number - 1))) {
            this.name = "Season " + number;
        }
        return this;
    }

    public SeasonBuilder withReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public SeasonBuilder withSeries(Series series) {
        this.series = series;
        return this;
    }

    public SeasonBuilder withEpisodes(List<Episode> episodes) {
        if (episodes != null && !episodes.isEmpty()) {
            this.episodes = new ArrayList<>(episodes);
        }
        return this;
    }

    public SeasonBuilder withEpisode(Episode episode) {
        this.episodes.add(episode);
        return this;
    }

    public SeasonBuilder withRandomEpisodes(int count) {
        return withGeneratedEpisodes(count, i -> new EpisodeBuilder()
                .withNumber(i + 1)
                .build());
    }

    public SeasonBuilder withGeneratedEpisodes(int count, Function<Integer, Episode> episodeGenerator) {
        for (int i = 0; i < count; i++) {
            this.episodes.add(episodeGenerator.apply(i));
        }
        return this;
    }

    public Season build() {
        Season season = new Season();
        season.setName(name);
        season.setNumber(number);
        season.setReleaseDate(releaseDate);
        season.setSeries(series);
        season.setEpisodes(episodes);

        episodes.forEach(episode -> episode.setSeason(season));

        return season;
    }
}
