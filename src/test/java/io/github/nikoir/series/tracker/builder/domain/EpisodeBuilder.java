package io.github.nikoir.series.tracker.builder.domain;

import io.github.nikoir.series.tracker.content.domain.entity.Episode;
import io.github.nikoir.series.tracker.content.domain.entity.Season;

import java.time.LocalDate;

public class EpisodeBuilder {
    private String name;
    private Integer number;
    private LocalDate releaseDate;
    private Season season;

    // Конструкторы
    public EpisodeBuilder() {
        // Значения по умолчанию
        this.number = 1;
        this.name = "Episode " + number;
        this.releaseDate = LocalDate.now();
    }

    // Базовые методы билдера
    public EpisodeBuilder withId(Long id) {
        // Обычно id не устанавливаем вручную для тестов
        return this;
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

    public Episode build() {
        Episode episode = new Episode();
        episode.setName(name);
        episode.setNumber(number);
        episode.setReleaseDate(releaseDate);
        episode.setSeason(season);
        return episode;
    }
}
