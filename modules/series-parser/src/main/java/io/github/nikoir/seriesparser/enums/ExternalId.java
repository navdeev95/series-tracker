package io.github.nikoir.seriesparser.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExternalId {
    KINOPOISK("kinopoisk"),
    IMDB("IMDB"),
    TMDB("TMDB"),
    MOVIELAB("movielab"),
    KINOPOISK_HD("kinopoisk_hd");
    private final String sourceName;
}
