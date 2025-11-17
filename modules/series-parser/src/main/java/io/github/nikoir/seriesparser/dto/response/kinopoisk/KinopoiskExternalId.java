package io.github.nikoir.seriesparser.dto.response.kinopoisk;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KinopoiskExternalId {
    IMDB("imdb"),
    TMDB("tmdb"),
    KINOPOISK_HD("kpHD");
    private final String name;
}
