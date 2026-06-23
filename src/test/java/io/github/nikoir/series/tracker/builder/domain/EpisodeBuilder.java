package io.github.nikoir.series.tracker.builder.domain;

import io.github.nikoir.series.tracker.content.domain.entity.Episode;
import io.github.nikoir.series.tracker.content.domain.entity.EpisodeRelease;
import io.github.nikoir.series.tracker.content.domain.entity.Season;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class EpisodeBuilder {
    private String name;
    private Integer number;
    private LocalDate releaseDate;
    private Season season;
    private List<EpisodeRelease> releases;

    // Конструкторы
    public EpisodeBuilder() {
        // Значения по умолчанию
        this.number = 1;
        this.name = "Episode " + number;
        this.releaseDate = LocalDate.now();
        this.releases = new ArrayList<>();
    }

    public EpisodeBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public EpisodeBuilder withNumber(Integer number) {
        this.number = number;
        if (this.name == null || this.name.equals("Episode " + (this.number - 1))) {
            this.name = "Episode " + number;
        }
        return this;
    }

    public EpisodeBuilder withReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public EpisodeBuilder withSeason(Season season) {
        this.season = season;
        return this;
    }

    public EpisodeBuilder withRelease(EpisodeRelease release) {
        this.releases.add(release);
        return this;
    }

    public Episode build() {
        Episode episode = new Episode();
        episode.setName(name);
        episode.setNumber(number);
        episode.setReleaseDate(releaseDate);
        episode.setSeason(season);
        episode.setReleases(releases);
        return episode;
    }
}
