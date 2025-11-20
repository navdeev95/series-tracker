package io.github.nikoir.seriesparser.dto.response.kinopoisk;

import io.github.nikoir.seriesparser.enums.ExternalId;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum KinopoiskExternalId {
    IMDB("imdb", ExternalId.IMDB),
    TMDB("tmdb", ExternalId.TMDB),
    KINOPOISK_HD("kpHD", ExternalId.KINOPOISK_HD);
    private final String name;
    private final ExternalId externalId;
}
